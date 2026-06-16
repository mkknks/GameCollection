package com.gamecollection.data.database;

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
import com.gamecollection.data.dao.CollectionItemDao;
import com.gamecollection.data.dao.CollectionItemDao_Impl;
import com.gamecollection.data.dao.GameMasterDao;
import com.gamecollection.data.dao.GameMasterDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class GameCollectionDatabase_Impl extends GameCollectionDatabase {
  private volatile GameMasterDao _gameMasterDao;

  private volatile CollectionItemDao _collectionItemDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `game_master` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `platform` TEXT, `publisher` TEXT, `releaseYear` INTEGER, `isUserAdded` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `collection_item` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `gameMasterId` INTEGER NOT NULL, `status` TEXT NOT NULL, `rating` INTEGER, `notes` TEXT, `addedAt` INTEGER NOT NULL, FOREIGN KEY(`gameMasterId`) REFERENCES `game_master`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_collection_item_gameMasterId` ON `collection_item` (`gameMasterId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '54ec9695108ffb3d4c61d11dc588b867')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `game_master`");
        db.execSQL("DROP TABLE IF EXISTS `collection_item`");
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
        db.execSQL("PRAGMA foreign_keys = ON");
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
        final HashMap<String, TableInfo.Column> _columnsGameMaster = new HashMap<String, TableInfo.Column>(6);
        _columnsGameMaster.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGameMaster.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGameMaster.put("platform", new TableInfo.Column("platform", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGameMaster.put("publisher", new TableInfo.Column("publisher", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGameMaster.put("releaseYear", new TableInfo.Column("releaseYear", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGameMaster.put("isUserAdded", new TableInfo.Column("isUserAdded", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysGameMaster = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesGameMaster = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoGameMaster = new TableInfo("game_master", _columnsGameMaster, _foreignKeysGameMaster, _indicesGameMaster);
        final TableInfo _existingGameMaster = TableInfo.read(db, "game_master");
        if (!_infoGameMaster.equals(_existingGameMaster)) {
          return new RoomOpenHelper.ValidationResult(false, "game_master(com.gamecollection.data.entity.GameMasterEntity).\n"
                  + " Expected:\n" + _infoGameMaster + "\n"
                  + " Found:\n" + _existingGameMaster);
        }
        final HashMap<String, TableInfo.Column> _columnsCollectionItem = new HashMap<String, TableInfo.Column>(6);
        _columnsCollectionItem.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollectionItem.put("gameMasterId", new TableInfo.Column("gameMasterId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollectionItem.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollectionItem.put("rating", new TableInfo.Column("rating", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollectionItem.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollectionItem.put("addedAt", new TableInfo.Column("addedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCollectionItem = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysCollectionItem.add(new TableInfo.ForeignKey("game_master", "CASCADE", "NO ACTION", Arrays.asList("gameMasterId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesCollectionItem = new HashSet<TableInfo.Index>(1);
        _indicesCollectionItem.add(new TableInfo.Index("index_collection_item_gameMasterId", true, Arrays.asList("gameMasterId"), Arrays.asList("ASC")));
        final TableInfo _infoCollectionItem = new TableInfo("collection_item", _columnsCollectionItem, _foreignKeysCollectionItem, _indicesCollectionItem);
        final TableInfo _existingCollectionItem = TableInfo.read(db, "collection_item");
        if (!_infoCollectionItem.equals(_existingCollectionItem)) {
          return new RoomOpenHelper.ValidationResult(false, "collection_item(com.gamecollection.data.entity.CollectionItemEntity).\n"
                  + " Expected:\n" + _infoCollectionItem + "\n"
                  + " Found:\n" + _existingCollectionItem);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "54ec9695108ffb3d4c61d11dc588b867", "7167fc6c4ab7541c78fccfba8f4312a8");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "game_master","collection_item");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `game_master`");
      _db.execSQL("DELETE FROM `collection_item`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
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
    _typeConvertersMap.put(GameMasterDao.class, GameMasterDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CollectionItemDao.class, CollectionItemDao_Impl.getRequiredConverters());
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
  public GameMasterDao gameMasterDao() {
    if (_gameMasterDao != null) {
      return _gameMasterDao;
    } else {
      synchronized(this) {
        if(_gameMasterDao == null) {
          _gameMasterDao = new GameMasterDao_Impl(this);
        }
        return _gameMasterDao;
      }
    }
  }

  @Override
  public CollectionItemDao collectionItemDao() {
    if (_collectionItemDao != null) {
      return _collectionItemDao;
    } else {
      synchronized(this) {
        if(_collectionItemDao == null) {
          _collectionItemDao = new CollectionItemDao_Impl(this);
        }
        return _collectionItemDao;
      }
    }
  }
}
