package com.agri.costtracker.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.agri.costtracker.data.model.ActivityRecord;
import com.agri.costtracker.data.model.Farmer;
import com.agri.costtracker.data.model.FarmerProfile;
import com.agri.costtracker.data.model.PaymentRecord;
import com.agri.costtracker.data.model.RecordCategory;
import com.agri.costtracker.data.model.RecordStatus;
import com.agri.costtracker.data.model.ServiceRates;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalArgumentException;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AgriDao_Impl implements AgriDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Farmer> __insertionAdapterOfFarmer;

  private final EntityInsertionAdapter<FarmerProfile> __insertionAdapterOfFarmerProfile;

  private final EntityInsertionAdapter<ServiceRates> __insertionAdapterOfServiceRates;

  private final EntityInsertionAdapter<ActivityRecord> __insertionAdapterOfActivityRecord;

  private final EntityInsertionAdapter<PaymentRecord> __insertionAdapterOfPaymentRecord;

  private final EntityDeletionOrUpdateAdapter<Farmer> __deletionAdapterOfFarmer;

  private final EntityDeletionOrUpdateAdapter<ActivityRecord> __deletionAdapterOfActivityRecord;

  private final EntityDeletionOrUpdateAdapter<PaymentRecord> __deletionAdapterOfPaymentRecord;

  private final EntityDeletionOrUpdateAdapter<Farmer> __updateAdapterOfFarmer;

  private final SharedSQLiteStatement __preparedStmtOfDeleteRecordsByFarmerId;

  private final SharedSQLiteStatement __preparedStmtOfDeletePaymentsByFarmerId;

  private final SharedSQLiteStatement __preparedStmtOfDeletePaymentsByRecordId;

  private final SharedSQLiteStatement __preparedStmtOfClearAllRecords;

  public AgriDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFarmer = new EntityInsertionAdapter<Farmer>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `farmers` (`id`,`name`,`mobile`,`village`,`totalAcres`,`notes`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Farmer entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getMobile());
        statement.bindString(4, entity.getVillage());
        statement.bindDouble(5, entity.getTotalAcres());
        statement.bindString(6, entity.getNotes());
        statement.bindLong(7, entity.getCreatedAt());
      }
    };
    this.__insertionAdapterOfFarmerProfile = new EntityInsertionAdapter<FarmerProfile>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `farmer_profile` (`id`,`fullName`,`totalOwnedAcres`,`cultivatedAcres`,`fallowAcres`,`sector`,`region`,`avatarUrl`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FarmerProfile entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getFullName());
        statement.bindDouble(3, entity.getTotalOwnedAcres());
        statement.bindDouble(4, entity.getCultivatedAcres());
        statement.bindDouble(5, entity.getFallowAcres());
        statement.bindString(6, entity.getSector());
        statement.bindString(7, entity.getRegion());
        statement.bindString(8, entity.getAvatarUrl());
      }
    };
    this.__insertionAdapterOfServiceRates = new EntityInsertionAdapter<ServiceRates>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `service_rates` (`id`,`sprayingRatePerAcre`,`cropCuttingRatePerAcre`,`globalTrendPercent`,`lastUpdated`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ServiceRates entity) {
        statement.bindLong(1, entity.getId());
        statement.bindDouble(2, entity.getSprayingRatePerAcre());
        statement.bindDouble(3, entity.getCropCuttingRatePerAcre());
        statement.bindDouble(4, entity.getGlobalTrendPercent());
        statement.bindLong(5, entity.getLastUpdated());
      }
    };
    this.__insertionAdapterOfActivityRecord = new EntityInsertionAdapter<ActivityRecord>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `activity_records` (`id`,`farmerId`,`farmerName`,`farmerMobile`,`title`,`date`,`location`,`cost`,`paidAmount`,`lastPaymentDate`,`acres`,`ratePerAcre`,`status`,`category`,`season`,`notes`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ActivityRecord entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getFarmerId());
        statement.bindString(3, entity.getFarmerName());
        statement.bindString(4, entity.getFarmerMobile());
        statement.bindString(5, entity.getTitle());
        statement.bindString(6, entity.getDate());
        statement.bindString(7, entity.getLocation());
        statement.bindDouble(8, entity.getCost());
        statement.bindDouble(9, entity.getPaidAmount());
        statement.bindString(10, entity.getLastPaymentDate());
        statement.bindDouble(11, entity.getAcres());
        statement.bindDouble(12, entity.getRatePerAcre());
        statement.bindString(13, __RecordStatus_enumToString(entity.getStatus()));
        statement.bindString(14, __RecordCategory_enumToString(entity.getCategory()));
        statement.bindString(15, entity.getSeason());
        statement.bindString(16, entity.getNotes());
        statement.bindLong(17, entity.getTimestamp());
      }
    };
    this.__insertionAdapterOfPaymentRecord = new EntityInsertionAdapter<PaymentRecord>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `payment_records` (`id`,`recordId`,`farmerId`,`farmerName`,`amount`,`date`,`paymentMode`,`notes`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PaymentRecord entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getRecordId());
        statement.bindLong(3, entity.getFarmerId());
        statement.bindString(4, entity.getFarmerName());
        statement.bindDouble(5, entity.getAmount());
        statement.bindString(6, entity.getDate());
        statement.bindString(7, entity.getPaymentMode());
        statement.bindString(8, entity.getNotes());
        statement.bindLong(9, entity.getTimestamp());
      }
    };
    this.__deletionAdapterOfFarmer = new EntityDeletionOrUpdateAdapter<Farmer>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `farmers` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Farmer entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__deletionAdapterOfActivityRecord = new EntityDeletionOrUpdateAdapter<ActivityRecord>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `activity_records` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ActivityRecord entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__deletionAdapterOfPaymentRecord = new EntityDeletionOrUpdateAdapter<PaymentRecord>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `payment_records` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PaymentRecord entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfFarmer = new EntityDeletionOrUpdateAdapter<Farmer>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `farmers` SET `id` = ?,`name` = ?,`mobile` = ?,`village` = ?,`totalAcres` = ?,`notes` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Farmer entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getMobile());
        statement.bindString(4, entity.getVillage());
        statement.bindDouble(5, entity.getTotalAcres());
        statement.bindString(6, entity.getNotes());
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteRecordsByFarmerId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM activity_records WHERE farmerId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeletePaymentsByFarmerId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM payment_records WHERE farmerId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeletePaymentsByRecordId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM payment_records WHERE recordId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAllRecords = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM activity_records";
        return _query;
      }
    };
  }

  @Override
  public Object insertFarmer(final Farmer farmer, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfFarmer.insertAndReturnId(farmer);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAllFarmers(final List<Farmer> farmers,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfFarmer.insert(farmers);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertOrUpdateProfile(final FarmerProfile profile,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfFarmerProfile.insert(profile);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertOrUpdateRates(final ServiceRates rates,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfServiceRates.insert(rates);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertRecord(final ActivityRecord record,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfActivityRecord.insertAndReturnId(record);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAllRecords(final List<ActivityRecord> records,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfActivityRecord.insert(records);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertPayment(final PaymentRecord payment,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfPaymentRecord.insertAndReturnId(payment);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAllPayments(final List<PaymentRecord> payments,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPaymentRecord.insert(payments);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteFarmer(final Farmer farmer, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfFarmer.handle(farmer);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteRecord(final ActivityRecord record,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfActivityRecord.handle(record);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePayment(final PaymentRecord payment,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfPaymentRecord.handle(payment);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateFarmer(final Farmer farmer, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfFarmer.handle(farmer);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteFarmerWithRelatedData(final Farmer farmer,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> AgriDao.DefaultImpls.deleteFarmerWithRelatedData(AgriDao_Impl.this, farmer, __cont), $completion);
  }

  @Override
  public Object deleteRecordWithPayments(final ActivityRecord record,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> AgriDao.DefaultImpls.deleteRecordWithPayments(AgriDao_Impl.this, record, __cont), $completion);
  }

  @Override
  public Object deleteRecordsByFarmerId(final long farmerId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteRecordsByFarmerId.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, farmerId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteRecordsByFarmerId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePaymentsByFarmerId(final long farmerId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeletePaymentsByFarmerId.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, farmerId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeletePaymentsByFarmerId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePaymentsByRecordId(final long recordId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeletePaymentsByRecordId.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, recordId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeletePaymentsByRecordId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAllRecords(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAllRecords.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAllRecords.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Farmer>> getAllFarmers() {
    final String _sql = "SELECT * FROM farmers ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"farmers"}, new Callable<List<Farmer>>() {
      @Override
      @NonNull
      public List<Farmer> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfMobile = CursorUtil.getColumnIndexOrThrow(_cursor, "mobile");
          final int _cursorIndexOfVillage = CursorUtil.getColumnIndexOrThrow(_cursor, "village");
          final int _cursorIndexOfTotalAcres = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAcres");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Farmer> _result = new ArrayList<Farmer>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Farmer _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpMobile;
            _tmpMobile = _cursor.getString(_cursorIndexOfMobile);
            final String _tmpVillage;
            _tmpVillage = _cursor.getString(_cursorIndexOfVillage);
            final double _tmpTotalAcres;
            _tmpTotalAcres = _cursor.getDouble(_cursorIndexOfTotalAcres);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new Farmer(_tmpId,_tmpName,_tmpMobile,_tmpVillage,_tmpTotalAcres,_tmpNotes,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Farmer> getFarmerById(final long id) {
    final String _sql = "SELECT * FROM farmers WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"farmers"}, new Callable<Farmer>() {
      @Override
      @Nullable
      public Farmer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfMobile = CursorUtil.getColumnIndexOrThrow(_cursor, "mobile");
          final int _cursorIndexOfVillage = CursorUtil.getColumnIndexOrThrow(_cursor, "village");
          final int _cursorIndexOfTotalAcres = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAcres");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final Farmer _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpMobile;
            _tmpMobile = _cursor.getString(_cursorIndexOfMobile);
            final String _tmpVillage;
            _tmpVillage = _cursor.getString(_cursorIndexOfVillage);
            final double _tmpTotalAcres;
            _tmpTotalAcres = _cursor.getDouble(_cursorIndexOfTotalAcres);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new Farmer(_tmpId,_tmpName,_tmpMobile,_tmpVillage,_tmpTotalAcres,_tmpNotes,_tmpCreatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<FarmerProfile> getFarmerProfile() {
    final String _sql = "SELECT * FROM farmer_profile WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"farmer_profile"}, new Callable<FarmerProfile>() {
      @Override
      @Nullable
      public FarmerProfile call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFullName = CursorUtil.getColumnIndexOrThrow(_cursor, "fullName");
          final int _cursorIndexOfTotalOwnedAcres = CursorUtil.getColumnIndexOrThrow(_cursor, "totalOwnedAcres");
          final int _cursorIndexOfCultivatedAcres = CursorUtil.getColumnIndexOrThrow(_cursor, "cultivatedAcres");
          final int _cursorIndexOfFallowAcres = CursorUtil.getColumnIndexOrThrow(_cursor, "fallowAcres");
          final int _cursorIndexOfSector = CursorUtil.getColumnIndexOrThrow(_cursor, "sector");
          final int _cursorIndexOfRegion = CursorUtil.getColumnIndexOrThrow(_cursor, "region");
          final int _cursorIndexOfAvatarUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "avatarUrl");
          final FarmerProfile _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpFullName;
            _tmpFullName = _cursor.getString(_cursorIndexOfFullName);
            final double _tmpTotalOwnedAcres;
            _tmpTotalOwnedAcres = _cursor.getDouble(_cursorIndexOfTotalOwnedAcres);
            final double _tmpCultivatedAcres;
            _tmpCultivatedAcres = _cursor.getDouble(_cursorIndexOfCultivatedAcres);
            final double _tmpFallowAcres;
            _tmpFallowAcres = _cursor.getDouble(_cursorIndexOfFallowAcres);
            final String _tmpSector;
            _tmpSector = _cursor.getString(_cursorIndexOfSector);
            final String _tmpRegion;
            _tmpRegion = _cursor.getString(_cursorIndexOfRegion);
            final String _tmpAvatarUrl;
            _tmpAvatarUrl = _cursor.getString(_cursorIndexOfAvatarUrl);
            _result = new FarmerProfile(_tmpId,_tmpFullName,_tmpTotalOwnedAcres,_tmpCultivatedAcres,_tmpFallowAcres,_tmpSector,_tmpRegion,_tmpAvatarUrl);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<ServiceRates> getServiceRates() {
    final String _sql = "SELECT * FROM service_rates WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"service_rates"}, new Callable<ServiceRates>() {
      @Override
      @Nullable
      public ServiceRates call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSprayingRatePerAcre = CursorUtil.getColumnIndexOrThrow(_cursor, "sprayingRatePerAcre");
          final int _cursorIndexOfCropCuttingRatePerAcre = CursorUtil.getColumnIndexOrThrow(_cursor, "cropCuttingRatePerAcre");
          final int _cursorIndexOfGlobalTrendPercent = CursorUtil.getColumnIndexOrThrow(_cursor, "globalTrendPercent");
          final int _cursorIndexOfLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdated");
          final ServiceRates _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final double _tmpSprayingRatePerAcre;
            _tmpSprayingRatePerAcre = _cursor.getDouble(_cursorIndexOfSprayingRatePerAcre);
            final double _tmpCropCuttingRatePerAcre;
            _tmpCropCuttingRatePerAcre = _cursor.getDouble(_cursorIndexOfCropCuttingRatePerAcre);
            final double _tmpGlobalTrendPercent;
            _tmpGlobalTrendPercent = _cursor.getDouble(_cursorIndexOfGlobalTrendPercent);
            final long _tmpLastUpdated;
            _tmpLastUpdated = _cursor.getLong(_cursorIndexOfLastUpdated);
            _result = new ServiceRates(_tmpId,_tmpSprayingRatePerAcre,_tmpCropCuttingRatePerAcre,_tmpGlobalTrendPercent,_tmpLastUpdated);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ActivityRecord>> getAllRecords() {
    final String _sql = "SELECT * FROM activity_records ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"activity_records"}, new Callable<List<ActivityRecord>>() {
      @Override
      @NonNull
      public List<ActivityRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFarmerId = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerId");
          final int _cursorIndexOfFarmerName = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerName");
          final int _cursorIndexOfFarmerMobile = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerMobile");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfCost = CursorUtil.getColumnIndexOrThrow(_cursor, "cost");
          final int _cursorIndexOfPaidAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAmount");
          final int _cursorIndexOfLastPaymentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPaymentDate");
          final int _cursorIndexOfAcres = CursorUtil.getColumnIndexOrThrow(_cursor, "acres");
          final int _cursorIndexOfRatePerAcre = CursorUtil.getColumnIndexOrThrow(_cursor, "ratePerAcre");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfSeason = CursorUtil.getColumnIndexOrThrow(_cursor, "season");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<ActivityRecord> _result = new ArrayList<ActivityRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ActivityRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpFarmerId;
            _tmpFarmerId = _cursor.getLong(_cursorIndexOfFarmerId);
            final String _tmpFarmerName;
            _tmpFarmerName = _cursor.getString(_cursorIndexOfFarmerName);
            final String _tmpFarmerMobile;
            _tmpFarmerMobile = _cursor.getString(_cursorIndexOfFarmerMobile);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpLocation;
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            final double _tmpCost;
            _tmpCost = _cursor.getDouble(_cursorIndexOfCost);
            final double _tmpPaidAmount;
            _tmpPaidAmount = _cursor.getDouble(_cursorIndexOfPaidAmount);
            final String _tmpLastPaymentDate;
            _tmpLastPaymentDate = _cursor.getString(_cursorIndexOfLastPaymentDate);
            final double _tmpAcres;
            _tmpAcres = _cursor.getDouble(_cursorIndexOfAcres);
            final double _tmpRatePerAcre;
            _tmpRatePerAcre = _cursor.getDouble(_cursorIndexOfRatePerAcre);
            final RecordStatus _tmpStatus;
            _tmpStatus = __RecordStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final RecordCategory _tmpCategory;
            _tmpCategory = __RecordCategory_stringToEnum(_cursor.getString(_cursorIndexOfCategory));
            final String _tmpSeason;
            _tmpSeason = _cursor.getString(_cursorIndexOfSeason);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new ActivityRecord(_tmpId,_tmpFarmerId,_tmpFarmerName,_tmpFarmerMobile,_tmpTitle,_tmpDate,_tmpLocation,_tmpCost,_tmpPaidAmount,_tmpLastPaymentDate,_tmpAcres,_tmpRatePerAcre,_tmpStatus,_tmpCategory,_tmpSeason,_tmpNotes,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ActivityRecord>> getRecordsByFarmer(final long farmerId) {
    final String _sql = "SELECT * FROM activity_records WHERE farmerId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, farmerId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"activity_records"}, new Callable<List<ActivityRecord>>() {
      @Override
      @NonNull
      public List<ActivityRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFarmerId = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerId");
          final int _cursorIndexOfFarmerName = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerName");
          final int _cursorIndexOfFarmerMobile = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerMobile");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfCost = CursorUtil.getColumnIndexOrThrow(_cursor, "cost");
          final int _cursorIndexOfPaidAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAmount");
          final int _cursorIndexOfLastPaymentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPaymentDate");
          final int _cursorIndexOfAcres = CursorUtil.getColumnIndexOrThrow(_cursor, "acres");
          final int _cursorIndexOfRatePerAcre = CursorUtil.getColumnIndexOrThrow(_cursor, "ratePerAcre");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfSeason = CursorUtil.getColumnIndexOrThrow(_cursor, "season");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<ActivityRecord> _result = new ArrayList<ActivityRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ActivityRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpFarmerId;
            _tmpFarmerId = _cursor.getLong(_cursorIndexOfFarmerId);
            final String _tmpFarmerName;
            _tmpFarmerName = _cursor.getString(_cursorIndexOfFarmerName);
            final String _tmpFarmerMobile;
            _tmpFarmerMobile = _cursor.getString(_cursorIndexOfFarmerMobile);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpLocation;
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            final double _tmpCost;
            _tmpCost = _cursor.getDouble(_cursorIndexOfCost);
            final double _tmpPaidAmount;
            _tmpPaidAmount = _cursor.getDouble(_cursorIndexOfPaidAmount);
            final String _tmpLastPaymentDate;
            _tmpLastPaymentDate = _cursor.getString(_cursorIndexOfLastPaymentDate);
            final double _tmpAcres;
            _tmpAcres = _cursor.getDouble(_cursorIndexOfAcres);
            final double _tmpRatePerAcre;
            _tmpRatePerAcre = _cursor.getDouble(_cursorIndexOfRatePerAcre);
            final RecordStatus _tmpStatus;
            _tmpStatus = __RecordStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final RecordCategory _tmpCategory;
            _tmpCategory = __RecordCategory_stringToEnum(_cursor.getString(_cursorIndexOfCategory));
            final String _tmpSeason;
            _tmpSeason = _cursor.getString(_cursorIndexOfSeason);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new ActivityRecord(_tmpId,_tmpFarmerId,_tmpFarmerName,_tmpFarmerMobile,_tmpTitle,_tmpDate,_tmpLocation,_tmpCost,_tmpPaidAmount,_tmpLastPaymentDate,_tmpAcres,_tmpRatePerAcre,_tmpStatus,_tmpCategory,_tmpSeason,_tmpNotes,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ActivityRecord>> getRecordsBySeason(final String season) {
    final String _sql = "SELECT * FROM activity_records WHERE season = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, season);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"activity_records"}, new Callable<List<ActivityRecord>>() {
      @Override
      @NonNull
      public List<ActivityRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFarmerId = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerId");
          final int _cursorIndexOfFarmerName = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerName");
          final int _cursorIndexOfFarmerMobile = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerMobile");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfCost = CursorUtil.getColumnIndexOrThrow(_cursor, "cost");
          final int _cursorIndexOfPaidAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAmount");
          final int _cursorIndexOfLastPaymentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPaymentDate");
          final int _cursorIndexOfAcres = CursorUtil.getColumnIndexOrThrow(_cursor, "acres");
          final int _cursorIndexOfRatePerAcre = CursorUtil.getColumnIndexOrThrow(_cursor, "ratePerAcre");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfSeason = CursorUtil.getColumnIndexOrThrow(_cursor, "season");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<ActivityRecord> _result = new ArrayList<ActivityRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ActivityRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpFarmerId;
            _tmpFarmerId = _cursor.getLong(_cursorIndexOfFarmerId);
            final String _tmpFarmerName;
            _tmpFarmerName = _cursor.getString(_cursorIndexOfFarmerName);
            final String _tmpFarmerMobile;
            _tmpFarmerMobile = _cursor.getString(_cursorIndexOfFarmerMobile);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpLocation;
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            final double _tmpCost;
            _tmpCost = _cursor.getDouble(_cursorIndexOfCost);
            final double _tmpPaidAmount;
            _tmpPaidAmount = _cursor.getDouble(_cursorIndexOfPaidAmount);
            final String _tmpLastPaymentDate;
            _tmpLastPaymentDate = _cursor.getString(_cursorIndexOfLastPaymentDate);
            final double _tmpAcres;
            _tmpAcres = _cursor.getDouble(_cursorIndexOfAcres);
            final double _tmpRatePerAcre;
            _tmpRatePerAcre = _cursor.getDouble(_cursorIndexOfRatePerAcre);
            final RecordStatus _tmpStatus;
            _tmpStatus = __RecordStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final RecordCategory _tmpCategory;
            _tmpCategory = __RecordCategory_stringToEnum(_cursor.getString(_cursorIndexOfCategory));
            final String _tmpSeason;
            _tmpSeason = _cursor.getString(_cursorIndexOfSeason);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new ActivityRecord(_tmpId,_tmpFarmerId,_tmpFarmerName,_tmpFarmerMobile,_tmpTitle,_tmpDate,_tmpLocation,_tmpCost,_tmpPaidAmount,_tmpLastPaymentDate,_tmpAcres,_tmpRatePerAcre,_tmpStatus,_tmpCategory,_tmpSeason,_tmpNotes,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ActivityRecord>> getRecordsByStatus(final RecordStatus status) {
    final String _sql = "SELECT * FROM activity_records WHERE status = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, __RecordStatus_enumToString(status));
    return CoroutinesRoom.createFlow(__db, false, new String[] {"activity_records"}, new Callable<List<ActivityRecord>>() {
      @Override
      @NonNull
      public List<ActivityRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFarmerId = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerId");
          final int _cursorIndexOfFarmerName = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerName");
          final int _cursorIndexOfFarmerMobile = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerMobile");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfCost = CursorUtil.getColumnIndexOrThrow(_cursor, "cost");
          final int _cursorIndexOfPaidAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAmount");
          final int _cursorIndexOfLastPaymentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPaymentDate");
          final int _cursorIndexOfAcres = CursorUtil.getColumnIndexOrThrow(_cursor, "acres");
          final int _cursorIndexOfRatePerAcre = CursorUtil.getColumnIndexOrThrow(_cursor, "ratePerAcre");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfSeason = CursorUtil.getColumnIndexOrThrow(_cursor, "season");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<ActivityRecord> _result = new ArrayList<ActivityRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ActivityRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpFarmerId;
            _tmpFarmerId = _cursor.getLong(_cursorIndexOfFarmerId);
            final String _tmpFarmerName;
            _tmpFarmerName = _cursor.getString(_cursorIndexOfFarmerName);
            final String _tmpFarmerMobile;
            _tmpFarmerMobile = _cursor.getString(_cursorIndexOfFarmerMobile);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpLocation;
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            final double _tmpCost;
            _tmpCost = _cursor.getDouble(_cursorIndexOfCost);
            final double _tmpPaidAmount;
            _tmpPaidAmount = _cursor.getDouble(_cursorIndexOfPaidAmount);
            final String _tmpLastPaymentDate;
            _tmpLastPaymentDate = _cursor.getString(_cursorIndexOfLastPaymentDate);
            final double _tmpAcres;
            _tmpAcres = _cursor.getDouble(_cursorIndexOfAcres);
            final double _tmpRatePerAcre;
            _tmpRatePerAcre = _cursor.getDouble(_cursorIndexOfRatePerAcre);
            final RecordStatus _tmpStatus;
            _tmpStatus = __RecordStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final RecordCategory _tmpCategory;
            _tmpCategory = __RecordCategory_stringToEnum(_cursor.getString(_cursorIndexOfCategory));
            final String _tmpSeason;
            _tmpSeason = _cursor.getString(_cursorIndexOfSeason);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new ActivityRecord(_tmpId,_tmpFarmerId,_tmpFarmerName,_tmpFarmerMobile,_tmpTitle,_tmpDate,_tmpLocation,_tmpCost,_tmpPaidAmount,_tmpLastPaymentDate,_tmpAcres,_tmpRatePerAcre,_tmpStatus,_tmpCategory,_tmpSeason,_tmpNotes,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<PaymentRecord>> getAllPayments() {
    final String _sql = "SELECT * FROM payment_records ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"payment_records"}, new Callable<List<PaymentRecord>>() {
      @Override
      @NonNull
      public List<PaymentRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRecordId = CursorUtil.getColumnIndexOrThrow(_cursor, "recordId");
          final int _cursorIndexOfFarmerId = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerId");
          final int _cursorIndexOfFarmerName = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerName");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfPaymentMode = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMode");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<PaymentRecord> _result = new ArrayList<PaymentRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PaymentRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpRecordId;
            _tmpRecordId = _cursor.getLong(_cursorIndexOfRecordId);
            final long _tmpFarmerId;
            _tmpFarmerId = _cursor.getLong(_cursorIndexOfFarmerId);
            final String _tmpFarmerName;
            _tmpFarmerName = _cursor.getString(_cursorIndexOfFarmerName);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpPaymentMode;
            _tmpPaymentMode = _cursor.getString(_cursorIndexOfPaymentMode);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new PaymentRecord(_tmpId,_tmpRecordId,_tmpFarmerId,_tmpFarmerName,_tmpAmount,_tmpDate,_tmpPaymentMode,_tmpNotes,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<PaymentRecord>> getPaymentsByFarmer(final long farmerId) {
    final String _sql = "SELECT * FROM payment_records WHERE farmerId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, farmerId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"payment_records"}, new Callable<List<PaymentRecord>>() {
      @Override
      @NonNull
      public List<PaymentRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRecordId = CursorUtil.getColumnIndexOrThrow(_cursor, "recordId");
          final int _cursorIndexOfFarmerId = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerId");
          final int _cursorIndexOfFarmerName = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerName");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfPaymentMode = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMode");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<PaymentRecord> _result = new ArrayList<PaymentRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PaymentRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpRecordId;
            _tmpRecordId = _cursor.getLong(_cursorIndexOfRecordId);
            final long _tmpFarmerId;
            _tmpFarmerId = _cursor.getLong(_cursorIndexOfFarmerId);
            final String _tmpFarmerName;
            _tmpFarmerName = _cursor.getString(_cursorIndexOfFarmerName);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpPaymentMode;
            _tmpPaymentMode = _cursor.getString(_cursorIndexOfPaymentMode);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new PaymentRecord(_tmpId,_tmpRecordId,_tmpFarmerId,_tmpFarmerName,_tmpAmount,_tmpDate,_tmpPaymentMode,_tmpNotes,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<PaymentRecord>> getPaymentsByRecord(final long recordId) {
    final String _sql = "SELECT * FROM payment_records WHERE recordId = ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, recordId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"payment_records"}, new Callable<List<PaymentRecord>>() {
      @Override
      @NonNull
      public List<PaymentRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRecordId = CursorUtil.getColumnIndexOrThrow(_cursor, "recordId");
          final int _cursorIndexOfFarmerId = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerId");
          final int _cursorIndexOfFarmerName = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerName");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfPaymentMode = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMode");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<PaymentRecord> _result = new ArrayList<PaymentRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PaymentRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpRecordId;
            _tmpRecordId = _cursor.getLong(_cursorIndexOfRecordId);
            final long _tmpFarmerId;
            _tmpFarmerId = _cursor.getLong(_cursorIndexOfFarmerId);
            final String _tmpFarmerName;
            _tmpFarmerName = _cursor.getString(_cursorIndexOfFarmerName);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpPaymentMode;
            _tmpPaymentMode = _cursor.getString(_cursorIndexOfPaymentMode);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new PaymentRecord(_tmpId,_tmpRecordId,_tmpFarmerId,_tmpFarmerName,_tmpAmount,_tmpDate,_tmpPaymentMode,_tmpNotes,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllFarmersSync(final Continuation<? super List<Farmer>> $completion) {
    final String _sql = "SELECT * FROM farmers";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Farmer>>() {
      @Override
      @NonNull
      public List<Farmer> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfMobile = CursorUtil.getColumnIndexOrThrow(_cursor, "mobile");
          final int _cursorIndexOfVillage = CursorUtil.getColumnIndexOrThrow(_cursor, "village");
          final int _cursorIndexOfTotalAcres = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAcres");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Farmer> _result = new ArrayList<Farmer>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Farmer _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpMobile;
            _tmpMobile = _cursor.getString(_cursorIndexOfMobile);
            final String _tmpVillage;
            _tmpVillage = _cursor.getString(_cursorIndexOfVillage);
            final double _tmpTotalAcres;
            _tmpTotalAcres = _cursor.getDouble(_cursorIndexOfTotalAcres);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new Farmer(_tmpId,_tmpName,_tmpMobile,_tmpVillage,_tmpTotalAcres,_tmpNotes,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllRecordsSync(final Continuation<? super List<ActivityRecord>> $completion) {
    final String _sql = "SELECT * FROM activity_records";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ActivityRecord>>() {
      @Override
      @NonNull
      public List<ActivityRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFarmerId = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerId");
          final int _cursorIndexOfFarmerName = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerName");
          final int _cursorIndexOfFarmerMobile = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerMobile");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfLocation = CursorUtil.getColumnIndexOrThrow(_cursor, "location");
          final int _cursorIndexOfCost = CursorUtil.getColumnIndexOrThrow(_cursor, "cost");
          final int _cursorIndexOfPaidAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "paidAmount");
          final int _cursorIndexOfLastPaymentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPaymentDate");
          final int _cursorIndexOfAcres = CursorUtil.getColumnIndexOrThrow(_cursor, "acres");
          final int _cursorIndexOfRatePerAcre = CursorUtil.getColumnIndexOrThrow(_cursor, "ratePerAcre");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfSeason = CursorUtil.getColumnIndexOrThrow(_cursor, "season");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<ActivityRecord> _result = new ArrayList<ActivityRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ActivityRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpFarmerId;
            _tmpFarmerId = _cursor.getLong(_cursorIndexOfFarmerId);
            final String _tmpFarmerName;
            _tmpFarmerName = _cursor.getString(_cursorIndexOfFarmerName);
            final String _tmpFarmerMobile;
            _tmpFarmerMobile = _cursor.getString(_cursorIndexOfFarmerMobile);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpLocation;
            _tmpLocation = _cursor.getString(_cursorIndexOfLocation);
            final double _tmpCost;
            _tmpCost = _cursor.getDouble(_cursorIndexOfCost);
            final double _tmpPaidAmount;
            _tmpPaidAmount = _cursor.getDouble(_cursorIndexOfPaidAmount);
            final String _tmpLastPaymentDate;
            _tmpLastPaymentDate = _cursor.getString(_cursorIndexOfLastPaymentDate);
            final double _tmpAcres;
            _tmpAcres = _cursor.getDouble(_cursorIndexOfAcres);
            final double _tmpRatePerAcre;
            _tmpRatePerAcre = _cursor.getDouble(_cursorIndexOfRatePerAcre);
            final RecordStatus _tmpStatus;
            _tmpStatus = __RecordStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final RecordCategory _tmpCategory;
            _tmpCategory = __RecordCategory_stringToEnum(_cursor.getString(_cursorIndexOfCategory));
            final String _tmpSeason;
            _tmpSeason = _cursor.getString(_cursorIndexOfSeason);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new ActivityRecord(_tmpId,_tmpFarmerId,_tmpFarmerName,_tmpFarmerMobile,_tmpTitle,_tmpDate,_tmpLocation,_tmpCost,_tmpPaidAmount,_tmpLastPaymentDate,_tmpAcres,_tmpRatePerAcre,_tmpStatus,_tmpCategory,_tmpSeason,_tmpNotes,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllPaymentsSync(final Continuation<? super List<PaymentRecord>> $completion) {
    final String _sql = "SELECT * FROM payment_records";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PaymentRecord>>() {
      @Override
      @NonNull
      public List<PaymentRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRecordId = CursorUtil.getColumnIndexOrThrow(_cursor, "recordId");
          final int _cursorIndexOfFarmerId = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerId");
          final int _cursorIndexOfFarmerName = CursorUtil.getColumnIndexOrThrow(_cursor, "farmerName");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfPaymentMode = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMode");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<PaymentRecord> _result = new ArrayList<PaymentRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PaymentRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpRecordId;
            _tmpRecordId = _cursor.getLong(_cursorIndexOfRecordId);
            final long _tmpFarmerId;
            _tmpFarmerId = _cursor.getLong(_cursorIndexOfFarmerId);
            final String _tmpFarmerName;
            _tmpFarmerName = _cursor.getString(_cursorIndexOfFarmerName);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpPaymentMode;
            _tmpPaymentMode = _cursor.getString(_cursorIndexOfPaymentMode);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new PaymentRecord(_tmpId,_tmpRecordId,_tmpFarmerId,_tmpFarmerName,_tmpAmount,_tmpDate,_tmpPaymentMode,_tmpNotes,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getRatesSync(final Continuation<? super ServiceRates> $completion) {
    final String _sql = "SELECT * FROM service_rates WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ServiceRates>() {
      @Override
      @Nullable
      public ServiceRates call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSprayingRatePerAcre = CursorUtil.getColumnIndexOrThrow(_cursor, "sprayingRatePerAcre");
          final int _cursorIndexOfCropCuttingRatePerAcre = CursorUtil.getColumnIndexOrThrow(_cursor, "cropCuttingRatePerAcre");
          final int _cursorIndexOfGlobalTrendPercent = CursorUtil.getColumnIndexOrThrow(_cursor, "globalTrendPercent");
          final int _cursorIndexOfLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdated");
          final ServiceRates _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final double _tmpSprayingRatePerAcre;
            _tmpSprayingRatePerAcre = _cursor.getDouble(_cursorIndexOfSprayingRatePerAcre);
            final double _tmpCropCuttingRatePerAcre;
            _tmpCropCuttingRatePerAcre = _cursor.getDouble(_cursorIndexOfCropCuttingRatePerAcre);
            final double _tmpGlobalTrendPercent;
            _tmpGlobalTrendPercent = _cursor.getDouble(_cursorIndexOfGlobalTrendPercent);
            final long _tmpLastUpdated;
            _tmpLastUpdated = _cursor.getLong(_cursorIndexOfLastUpdated);
            _result = new ServiceRates(_tmpId,_tmpSprayingRatePerAcre,_tmpCropCuttingRatePerAcre,_tmpGlobalTrendPercent,_tmpLastUpdated);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getProfileSync(final Continuation<? super FarmerProfile> $completion) {
    final String _sql = "SELECT * FROM farmer_profile WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<FarmerProfile>() {
      @Override
      @Nullable
      public FarmerProfile call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFullName = CursorUtil.getColumnIndexOrThrow(_cursor, "fullName");
          final int _cursorIndexOfTotalOwnedAcres = CursorUtil.getColumnIndexOrThrow(_cursor, "totalOwnedAcres");
          final int _cursorIndexOfCultivatedAcres = CursorUtil.getColumnIndexOrThrow(_cursor, "cultivatedAcres");
          final int _cursorIndexOfFallowAcres = CursorUtil.getColumnIndexOrThrow(_cursor, "fallowAcres");
          final int _cursorIndexOfSector = CursorUtil.getColumnIndexOrThrow(_cursor, "sector");
          final int _cursorIndexOfRegion = CursorUtil.getColumnIndexOrThrow(_cursor, "region");
          final int _cursorIndexOfAvatarUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "avatarUrl");
          final FarmerProfile _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpFullName;
            _tmpFullName = _cursor.getString(_cursorIndexOfFullName);
            final double _tmpTotalOwnedAcres;
            _tmpTotalOwnedAcres = _cursor.getDouble(_cursorIndexOfTotalOwnedAcres);
            final double _tmpCultivatedAcres;
            _tmpCultivatedAcres = _cursor.getDouble(_cursorIndexOfCultivatedAcres);
            final double _tmpFallowAcres;
            _tmpFallowAcres = _cursor.getDouble(_cursorIndexOfFallowAcres);
            final String _tmpSector;
            _tmpSector = _cursor.getString(_cursorIndexOfSector);
            final String _tmpRegion;
            _tmpRegion = _cursor.getString(_cursorIndexOfRegion);
            final String _tmpAvatarUrl;
            _tmpAvatarUrl = _cursor.getString(_cursorIndexOfAvatarUrl);
            _result = new FarmerProfile(_tmpId,_tmpFullName,_tmpTotalOwnedAcres,_tmpCultivatedAcres,_tmpFallowAcres,_tmpSector,_tmpRegion,_tmpAvatarUrl);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private String __RecordStatus_enumToString(@NonNull final RecordStatus _value) {
    switch (_value) {
      case COMPLETED: return "COMPLETED";
      case INVOICED: return "INVOICED";
      case PARTIAL: return "PARTIAL";
      case ARCHIVED: return "ARCHIVED";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private String __RecordCategory_enumToString(@NonNull final RecordCategory _value) {
    switch (_value) {
      case SPRAYING: return "SPRAYING";
      case HARVESTING: return "HARVESTING";
      case LAND: return "LAND";
      case SEEDS: return "SEEDS";
      case MAINTENANCE: return "MAINTENANCE";
      case IRRIGATION: return "IRRIGATION";
      case OTHER: return "OTHER";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private RecordStatus __RecordStatus_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "COMPLETED": return RecordStatus.COMPLETED;
      case "INVOICED": return RecordStatus.INVOICED;
      case "PARTIAL": return RecordStatus.PARTIAL;
      case "ARCHIVED": return RecordStatus.ARCHIVED;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }

  private RecordCategory __RecordCategory_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "SPRAYING": return RecordCategory.SPRAYING;
      case "HARVESTING": return RecordCategory.HARVESTING;
      case "LAND": return RecordCategory.LAND;
      case "SEEDS": return RecordCategory.SEEDS;
      case "MAINTENANCE": return RecordCategory.MAINTENANCE;
      case "IRRIGATION": return RecordCategory.IRRIGATION;
      case "OTHER": return RecordCategory.OTHER;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
