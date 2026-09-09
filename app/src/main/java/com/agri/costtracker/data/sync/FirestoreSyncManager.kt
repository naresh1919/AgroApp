package com.agri.costtracker.data.sync

import android.content.Context
import android.app.Activity
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.agri.costtracker.R
import com.agri.costtracker.data.local.AgriDao
import com.agri.costtracker.data.model.*
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.SetOptions
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

enum class CloudSyncState {
    IDLE,
    SYNCING,
    SUCCESS,
    ERROR
}

data class SignedInUser(
    val uid: String,
    val displayName: String,
    val email: String
)

class FirestoreSyncManager(private val context: Context) {

    private val TAG = "FirestoreSyncManager"

    private val _syncState = MutableStateFlow(CloudSyncState.IDLE)
    val syncState: StateFlow<CloudSyncState> = _syncState.asStateFlow()

    private val _lastSyncMessage = MutableStateFlow("Ready to sync")
    val lastSyncMessage: StateFlow<String> = _lastSyncMessage.asStateFlow()

    private val _signedInUser = MutableStateFlow<SignedInUser?>(null)
    val signedInUser: StateFlow<SignedInUser?> = _signedInUser.asStateFlow()

    private val _isAuthenticating = MutableStateFlow(false)
    val isAuthenticating: StateFlow<Boolean> = _isAuthenticating.asStateFlow()

    private val _authenticationError = MutableStateFlow<String?>(null)
    val authenticationError: StateFlow<String?> = _authenticationError.asStateFlow()

    private var firestore: FirebaseFirestore? = null
    private var auth: FirebaseAuth? = null

    init {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                val db = FirebaseFirestore.getInstance()
                // Enable offline cache persistence
                val settings = FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build()
                db.firestoreSettings = settings
                firestore = db
                auth = FirebaseAuth.getInstance().also { firebaseAuth ->
                    _signedInUser.value = firebaseAuth.currentUser?.toSignedInUser()
                    firebaseAuth.addAuthStateListener { activeAuth ->
                        _signedInUser.value = activeAuth.currentUser?.toSignedInUser()
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase initialization skipped or pending setup: ${e.message}")
        }
    }

    suspend fun signInWithGoogle(activity: Activity): Result<Unit> {
        val firebaseAuth = auth ?: return Result.failure(IllegalStateException("Firebase is not configured."))
        _isAuthenticating.value = true
        _authenticationError.value = null
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(true)
                .build()
            val credential = CredentialManager.create(context)
                .getCredential(activity, GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build())
                .credential

            if (credential !is CustomCredential || credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                error("Select a Google account to continue.")
            }
            val token = GoogleIdTokenCredential.createFrom(credential.data).idToken
            firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(token, null)).await()
            Result.success(Unit)
        } catch (e: GetCredentialException) {
            val message = "Google sign-in was cancelled or unavailable."
            _authenticationError.value = message
            Result.failure(IllegalStateException(message, e))
        } catch (e: Exception) {
            val message = e.localizedMessage ?: "Unable to sign in with Google."
            _authenticationError.value = message
            Result.failure(e)
        } finally {
            _isAuthenticating.value = false
        }
    }

    fun signOut() {
        auth?.signOut()
        _authenticationError.value = null
        _lastSyncMessage.value = "Signed out. Local data remains on this device."
    }

    private fun getTenantUserId(): String =
        auth?.currentUser?.uid ?: throw IllegalStateException("Sign in with Google before using cloud sync.")

    private fun com.google.firebase.auth.FirebaseUser.toSignedInUser() = SignedInUser(
        uid = uid,
        displayName = displayName ?: "Agri operator",
        email = email ?: "Google account"
    )

    suspend fun backupToCloud(dao: AgriDao): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val db = firestore
        if (db == null) {
            _syncState.value = CloudSyncState.ERROR
            val msg = "Firebase not yet linked. Place google-services.json from Firebase Console."
            _lastSyncMessage.value = msg
            return@withContext Pair(false, msg)
        }

        _syncState.value = CloudSyncState.SYNCING
        _lastSyncMessage.value = "Uploading records to Cloud Firestore..."

        try {
            val uid = getTenantUserId()
            val tenantRef = db.collection("agro_tenants").document(uid)

            val farmers = dao.getAllFarmersSync()
            val records = dao.getAllRecordsSync()
            val payments = dao.getAllPaymentsSync()
            val rates = dao.getRatesSync()
            val profile = dao.getProfileSync()

            val batch = db.batch()

            // 1. Profile & Rates
            if (profile != null) {
                batch.set(tenantRef.collection("profile").document("main"), profile, SetOptions.merge())
            }
            if (rates != null) {
                batch.set(tenantRef.collection("rates").document("config"), rates, SetOptions.merge())
            }

            // 2. Farmers
            farmers.forEach { farmer ->
                val doc = tenantRef.collection("farmers").document(farmer.id.toString())
                batch.set(doc, farmer, SetOptions.merge())
            }

            // 3. Activity Bookings
            records.forEach { record ->
                val doc = tenantRef.collection("records").document(record.id.toString())
                batch.set(doc, record, SetOptions.merge())
            }

            // 4. Payments
            payments.forEach { payment ->
                val doc = tenantRef.collection("payments").document(payment.id.toString())
                batch.set(doc, payment, SetOptions.merge())
            }

            batch.commit().await()

            _syncState.value = CloudSyncState.SUCCESS
            val successMsg = "Synced ${farmers.size} farmers, ${records.size} bookings, and ${payments.size} payments to Cloud!"
            _lastSyncMessage.value = successMsg
            Pair(true, successMsg)
        } catch (e: Exception) {
            Log.e(TAG, "Cloud backup error: ${e.message}", e)
            _syncState.value = CloudSyncState.ERROR
            val errorMsg = "Sync failed: ${e.localizedMessage ?: "Network error"}"
            _lastSyncMessage.value = errorMsg
            Pair(false, errorMsg)
        }
    }

    suspend fun restoreFromCloud(dao: AgriDao): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val db = firestore
        if (db == null) {
            _syncState.value = CloudSyncState.ERROR
            val msg = "Firebase not yet linked. Place google-services.json from Firebase Console."
            _lastSyncMessage.value = msg
            return@withContext Pair(false, msg)
        }

        _syncState.value = CloudSyncState.SYNCING
        _lastSyncMessage.value = "Fetching records from Cloud Firestore..."

        try {
            val uid = getTenantUserId()
            val tenantRef = db.collection("agro_tenants").document(uid)

            // 1. Fetch Farmers
            val farmersSnapshot = tenantRef.collection("farmers").get().await()
            val cloudFarmers = farmersSnapshot.toObjects(Farmer::class.java)
            if (cloudFarmers.isNotEmpty()) {
                dao.insertAllFarmers(cloudFarmers)
            }

            // 2. Fetch Records
            val recordsSnapshot = tenantRef.collection("records").get().await()
            val cloudRecords = recordsSnapshot.toObjects(ActivityRecord::class.java)
            if (cloudRecords.isNotEmpty()) {
                dao.insertAllRecords(cloudRecords)
            }

            // 3. Fetch Payments
            val paymentsSnapshot = tenantRef.collection("payments").get().await()
            val cloudPayments = paymentsSnapshot.toObjects(PaymentRecord::class.java)
            if (cloudPayments.isNotEmpty()) {
                dao.insertAllPayments(cloudPayments)
            }

            // 4. Fetch Rates
            val ratesDoc = tenantRef.collection("rates").document("config").get().await()
            ratesDoc.toObject(ServiceRates::class.java)?.let { dao.insertOrUpdateRates(it) }

            // 5. Fetch Profile
            val profileDoc = tenantRef.collection("profile").document("main").get().await()
            profileDoc.toObject(FarmerProfile::class.java)?.let { dao.insertOrUpdateProfile(it) }

            _syncState.value = CloudSyncState.SUCCESS
            val successMsg = "Restored ${cloudFarmers.size} farmers and ${cloudRecords.size} bookings from Cloud!"
            _lastSyncMessage.value = successMsg
            Pair(true, successMsg)
        } catch (e: Exception) {
            Log.e(TAG, "Cloud restore error: ${e.message}", e)
            _syncState.value = CloudSyncState.ERROR
            val errorMsg = "Restore failed: ${e.localizedMessage ?: "Network error"}"
            _lastSyncMessage.value = errorMsg
            Pair(false, errorMsg)
        }
    }
}
