package com.agri.costtracker.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AgriDatabase_Impl extends AgriDatabase {
  private volatile AgriDao _agriDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `farmers` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `mobile` TEXT NOT NULL, `village` TEXT NOT NULL, `totalAcres` REAL NOT NULL, `notes` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `farmer_profile` (`id` INTEGER NOT NULL, `fullName` TEXT NOT NULL, `totalOwnedAcres` REAL NOT NULL, `cultivatedAcres` REAL NOT NULL, `fallowAcres` REAL NOT NULL, `sector` TEXT NOT NULL, `region` TEXT NOT NULL, `avatarUrl` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `service_rates` (`id` INTEGER NOT NULL, `sprayingRatePerAcre` REAL NOT NULL, `cropCuttingRatePerAcre` REAL NOT NULL, `globalTrendPercent` REAL NOT NULL, `lastUpdated` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `activity_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `farmerId` INTEGER NOT NULL, `farmerName` TEXT NOT NULL, `farmerMobile` TEXT NOT NULL, `title` TEXT NOT NULL, `date` TEXT NOT NULL, `location` TEXT NOT NULL, `cost` REAL NOT NULL, `acres` REAL NOT NULL, `ratePerAcre` REAL NOT NULL, `status` TEXT NOT NULL, `category` TEXT NOT NULL, `season` TEXT NOT NULL, `notes` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'bf3b662ce0b6519c00b94eb68d3b6f8c')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `farmers`");
        db.execSQL("DROP TABLE IF EXISTS `farmer_profile`");
        db.execSQL("DROP TABLE IF EXISTS `service_rates`");
        db.execSQL("DROP TABLE IF EXISTS `activity_records`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsFarmers = new HashMap<String, TableInfo.Column>(7);
        _columnsFarmers.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFarmers.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFarmers.put("mobile", new TableInfo.Column("mobile", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFarmers.put("village", new TableInfo.Column("village", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFarmers.put("totalAcres", new TableInfo.Column("totalAcres", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFarmers.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFarmers.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFarmers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFarmers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFarmers = new TableInfo("farmers", _columnsFarmers, _foreignKeysFarmers, _indicesFarmers);
        final TableInfo _existingFarmers = TableInfo.read(db, "farmers");
        if (!_infoFarmers.equals(_existingFarmers)) {
          return new RoomOpenHelper.ValidationResult(false, "farmers(com.agri.costtracker.data.model.Farmer).\n"
                  + " Expected:\n" + _infoFarmers + "\n"
                  + " Found:\n" + _existingFarmers);
        }
        final HashMap<String, TableInfo.Column> _columnsFarmerProfile = new HashMap<String, TableInfo.Column>(8);
        _columnsFarmerProfile.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFarmerProfile.put("fullName", new TableInfo.Column("fullName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFarmerProfile.put("totalOwnedAcres", new TableInfo.Column("totalOwnedAcres", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFarmerProfile.put("cultivatedAcres", new TableInfo.Column("cultivatedAcres", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFarmerProfile.put("fallowAcres", new TableInfo.Column("fallowAcres", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFarmerProfile.put("sector", new TableInfo.Column("sector", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFarmerProfile.put("region", new TableInfo.Column("region", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFarmerProfile.put("avatarUrl", new TableInfo.Column("avatarUrl", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFarmerProfile = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFarmerProfile = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFarmerProfile = new TableInfo("farmer_profile", _columnsFarmerProfile, _foreignKeysFarmerProfile, _indicesFarmerProfile);
        final TableInfo _existingFarmerProfile = TableInfo.read(db, "farmer_profile");
        if (!_infoFarmerProfile.equals(_existingFarmerProfile)) {
          return new RoomOpenHelper.ValidationResult(false, "farmer_profile(com.agri.costtracker.data.model.FarmerProfile).\n"
                  + " Expected:\n" + _infoFarmerProfile + "\n"
                  + " Found:\n" + _existingFarmerProfile);
        }
        final HashMap<String, TableInfo.Column> _columnsServiceRates = new HashMap<String, TableInfo.Column>(5);
        _columnsServiceRates.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceRates.put("sprayingRatePerAcre", new TableInfo.Column("sprayingRatePerAcre", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceRates.put("cropCuttingRatePerAcre", new TableInfo.Column("cropCuttingRatePerAcre", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceRates.put("globalTrendPercent", new TableInfo.Column("globalTrendPercent", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsServiceRates.put("lastUpdated", new TableInfo.Column("lastUpdated", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysServiceRates = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesServiceRates = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoServiceRates = new TableInfo("service_rates", _columnsServiceRates, _foreignKeysServiceRates, _indicesServiceRates);
        final TableInfo _existingServiceRates = TableInfo.read(db, "service_rates");
        if (!_infoServiceRates.equals(_existingServiceRates)) {
          return new RoomOpenHelper.ValidationResult(false, "service_rates(com.agri.costtracker.data.model.ServiceRates).\n"
                  + " Expected:\n" + _infoServiceRates + "\n"
                  + " Found:\n" + _existingServiceRates);
        }
        final HashMap<String, TableInfo.Column> _columnsActivityRecords = new HashMap<String, TableInfo.Column>(15);
        _columnsActivityRecords.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivityRecords.put("farmerId", new TableInfo.Column("farmerId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivityRecords.put("farmerName", new TableInfo.Column("farmerName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivityRecords.put("farmerMobile", new TableInfo.Column("farmerMobile", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivityRecords.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivityRecords.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivityRecords.put("location", new TableInfo.Column("location", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivityRecords.put("cost", new TableInfo.Column("cost", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivityRecords.put("acres", new TableInfo.Column("acres", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivityRecords.put("ratePerAcre", new TableInfo.Column("ratePerAcre", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivityRecords.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivityRecords.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivityRecords.put("season", new TableInfo.Column("season", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivityRecords.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivityRecords.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysActivityRecords = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesActivityRecords = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoActivityRecords = new TableInfo("activity_records", _columnsActivityRecords, _foreignKeysActivityRecords, _indicesActivityRecords);
        final TableInfo _existingActivityRecords = TableInfo.read(db, "activity_records");
        if (!_infoActivityRecords.equals(_existingActivityRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "activity_records(com.agri.costtracker.data.model.ActivityRecord).\n"
                  + " Expected:\n" + _infoActivityRecords + "\n"
                  + " Found:\n" + _existingActivityRecords);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "bf3b662ce0b6519c00b94eb68d3b6f8c", "0a8f54cf2d64b88322926a348ac16879");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "farmers","farmer_profile","service_rates","activity_records");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `farmers`");
      _db.execSQL("DELETE FROM `farmer_profile`");
      _db.execSQL("DELETE FROM `service_rates`");
      _db.execSQL("DELETE FROM `activity_records`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(AgriDao.class, AgriDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public AgriDao agriDao() {
    if (_agriDao != null) {
      return _agriDao;
    } else {
      synchronized(this) {
        if(_agriDao == null) {
          _agriDao = new AgriDao_Impl(this);
        }
        return _agriDao;
      }
    }
  }
}
