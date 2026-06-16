package com.gamecollection.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.gamecollection.data.database.Converters;
import com.gamecollection.data.entity.CollectionItemEntity;
import com.gamecollection.data.entity.GameMasterEntity;
import com.gamecollection.data.model.CollectionStatus;
import com.gamecollection.data.model.GameWithMaster;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
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
public final class CollectionItemDao_Impl implements CollectionItemDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CollectionItemEntity> __insertionAdapterOfCollectionItemEntity;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<CollectionItemEntity> __deletionAdapterOfCollectionItemEntity;

  private final EntityDeletionOrUpdateAdapter<CollectionItemEntity> __updateAdapterOfCollectionItemEntity;

  public CollectionItemDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCollectionItemEntity = new EntityInsertionAdapter<CollectionItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `collection_item` (`id`,`gameMasterId`,`status`,`rating`,`notes`,`addedAt`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CollectionItemEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getGameMasterId());
        final String _tmp = __converters.fromCollectionStatus(entity.getStatus());
        statement.bindString(3, _tmp);
        if (entity.getRating() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getRating());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getNotes());
        }
        statement.bindLong(6, entity.getAddedAt());
      }
    };
    this.__deletionAdapterOfCollectionItemEntity = new EntityDeletionOrUpdateAdapter<CollectionItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `collection_item` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CollectionItemEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfCollectionItemEntity = new EntityDeletionOrUpdateAdapter<CollectionItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `collection_item` SET `id` = ?,`gameMasterId` = ?,`status` = ?,`rating` = ?,`notes` = ?,`addedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CollectionItemEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getGameMasterId());
        final String _tmp = __converters.fromCollectionStatus(entity.getStatus());
        statement.bindString(3, _tmp);
        if (entity.getRating() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getRating());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getNotes());
        }
        statement.bindLong(6, entity.getAddedAt());
        statement.bindLong(7, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final CollectionItemEntity item,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfCollectionItemEntity.insertAndReturnId(item);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final CollectionItemEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfCollectionItemEntity.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final CollectionItemEntity item,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfCollectionItemEntity.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<GameWithMaster>> observeAllWithMaster() {
    final String _sql = "SELECT * FROM collection_item ORDER BY addedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"game_master",
        "collection_item"}, new Callable<List<GameWithMaster>>() {
      @Override
      @NonNull
      public List<GameWithMaster> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfGameMasterId = CursorUtil.getColumnIndexOrThrow(_cursor, "gameMasterId");
            final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
            final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
            final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
            final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "addedAt");
            final LongSparseArray<GameMasterEntity> _collectionGameMaster = new LongSparseArray<GameMasterEntity>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfGameMasterId);
              _collectionGameMaster.put(_tmpKey, null);
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipgameMasterAscomGamecollectionDataEntityGameMasterEntity(_collectionGameMaster);
            final List<GameWithMaster> _result = new ArrayList<GameWithMaster>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final GameWithMaster _item;
              final CollectionItemEntity _tmpCollectionItem;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final long _tmpGameMasterId;
              _tmpGameMasterId = _cursor.getLong(_cursorIndexOfGameMasterId);
              final CollectionStatus _tmpStatus;
              final String _tmp;
              _tmp = _cursor.getString(_cursorIndexOfStatus);
              _tmpStatus = __converters.toCollectionStatus(_tmp);
              final Integer _tmpRating;
              if (_cursor.isNull(_cursorIndexOfRating)) {
                _tmpRating = null;
              } else {
                _tmpRating = _cursor.getInt(_cursorIndexOfRating);
              }
              final String _tmpNotes;
              if (_cursor.isNull(_cursorIndexOfNotes)) {
                _tmpNotes = null;
              } else {
                _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
              }
              final long _tmpAddedAt;
              _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
              _tmpCollectionItem = new CollectionItemEntity(_tmpId,_tmpGameMasterId,_tmpStatus,_tmpRating,_tmpNotes,_tmpAddedAt);
              final GameMasterEntity _tmpGameMaster;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfGameMasterId);
              _tmpGameMaster = _collectionGameMaster.get(_tmpKey_1);
              _item = new GameWithMaster(_tmpCollectionItem,_tmpGameMaster);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<GameWithMaster> observeWithMasterById(final long id) {
    final String _sql = "SELECT * FROM collection_item WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"game_master",
        "collection_item"}, new Callable<GameWithMaster>() {
      @Override
      @Nullable
      public GameWithMaster call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfGameMasterId = CursorUtil.getColumnIndexOrThrow(_cursor, "gameMasterId");
            final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
            final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
            final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
            final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "addedAt");
            final LongSparseArray<GameMasterEntity> _collectionGameMaster = new LongSparseArray<GameMasterEntity>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfGameMasterId);
              _collectionGameMaster.put(_tmpKey, null);
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipgameMasterAscomGamecollectionDataEntityGameMasterEntity(_collectionGameMaster);
            final GameWithMaster _result;
            if (_cursor.moveToFirst()) {
              final CollectionItemEntity _tmpCollectionItem;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final long _tmpGameMasterId;
              _tmpGameMasterId = _cursor.getLong(_cursorIndexOfGameMasterId);
              final CollectionStatus _tmpStatus;
              final String _tmp;
              _tmp = _cursor.getString(_cursorIndexOfStatus);
              _tmpStatus = __converters.toCollectionStatus(_tmp);
              final Integer _tmpRating;
              if (_cursor.isNull(_cursorIndexOfRating)) {
                _tmpRating = null;
              } else {
                _tmpRating = _cursor.getInt(_cursorIndexOfRating);
              }
              final String _tmpNotes;
              if (_cursor.isNull(_cursorIndexOfNotes)) {
                _tmpNotes = null;
              } else {
                _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
              }
              final long _tmpAddedAt;
              _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
              _tmpCollectionItem = new CollectionItemEntity(_tmpId,_tmpGameMasterId,_tmpStatus,_tmpRating,_tmpNotes,_tmpAddedAt);
              final GameMasterEntity _tmpGameMaster;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfGameMasterId);
              _tmpGameMaster = _collectionGameMaster.get(_tmpKey_1);
              _result = new GameWithMaster(_tmpCollectionItem,_tmpGameMaster);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Boolean> observeIsInCollection(final long gameMasterId) {
    final String _sql = "SELECT EXISTS(SELECT 1 FROM collection_item WHERE gameMasterId = ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, gameMasterId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"collection_item"}, new Callable<Boolean>() {
      @Override
      @NonNull
      public Boolean call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Boolean _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp != 0;
          } else {
            _result = false;
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
  public Object isInCollection(final long gameMasterId,
      final Continuation<? super Boolean> $completion) {
    final String _sql = "SELECT EXISTS(SELECT 1 FROM collection_item WHERE gameMasterId = ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, gameMasterId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Boolean>() {
      @Override
      @NonNull
      public Boolean call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Boolean _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp != 0;
          } else {
            _result = false;
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
  public Object getById(final long id,
      final Continuation<? super CollectionItemEntity> $completion) {
    final String _sql = "SELECT * FROM collection_item WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CollectionItemEntity>() {
      @Override
      @Nullable
      public CollectionItemEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGameMasterId = CursorUtil.getColumnIndexOrThrow(_cursor, "gameMasterId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfAddedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "addedAt");
          final CollectionItemEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpGameMasterId;
            _tmpGameMasterId = _cursor.getLong(_cursorIndexOfGameMasterId);
            final CollectionStatus _tmpStatus;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfStatus);
            _tmpStatus = __converters.toCollectionStatus(_tmp);
            final Integer _tmpRating;
            if (_cursor.isNull(_cursorIndexOfRating)) {
              _tmpRating = null;
            } else {
              _tmpRating = _cursor.getInt(_cursorIndexOfRating);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final long _tmpAddedAt;
            _tmpAddedAt = _cursor.getLong(_cursorIndexOfAddedAt);
            _result = new CollectionItemEntity(_tmpId,_tmpGameMasterId,_tmpStatus,_tmpRating,_tmpNotes,_tmpAddedAt);
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

  private void __fetchRelationshipgameMasterAscomGamecollectionDataEntityGameMasterEntity(
      @NonNull final LongSparseArray<GameMasterEntity> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, false, (map) -> {
        __fetchRelationshipgameMasterAscomGamecollectionDataEntityGameMasterEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`title`,`platform`,`publisher`,`releaseYear`,`isUserAdded` FROM `game_master` WHERE `id` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "id");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfTitle = 1;
      final int _cursorIndexOfPlatform = 2;
      final int _cursorIndexOfPublisher = 3;
      final int _cursorIndexOfReleaseYear = 4;
      final int _cursorIndexOfIsUserAdded = 5;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        if (_map.containsKey(_tmpKey)) {
          final GameMasterEntity _item_1;
          final long _tmpId;
          _tmpId = _cursor.getLong(_cursorIndexOfId);
          final String _tmpTitle;
          _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
          final String _tmpPlatform;
          if (_cursor.isNull(_cursorIndexOfPlatform)) {
            _tmpPlatform = null;
          } else {
            _tmpPlatform = _cursor.getString(_cursorIndexOfPlatform);
          }
          final String _tmpPublisher;
          if (_cursor.isNull(_cursorIndexOfPublisher)) {
            _tmpPublisher = null;
          } else {
            _tmpPublisher = _cursor.getString(_cursorIndexOfPublisher);
          }
          final Integer _tmpReleaseYear;
          if (_cursor.isNull(_cursorIndexOfReleaseYear)) {
            _tmpReleaseYear = null;
          } else {
            _tmpReleaseYear = _cursor.getInt(_cursorIndexOfReleaseYear);
          }
          final boolean _tmpIsUserAdded;
          final int _tmp;
          _tmp = _cursor.getInt(_cursorIndexOfIsUserAdded);
          _tmpIsUserAdded = _tmp != 0;
          _item_1 = new GameMasterEntity(_tmpId,_tmpTitle,_tmpPlatform,_tmpPublisher,_tmpReleaseYear,_tmpIsUserAdded);
          _map.put(_tmpKey, _item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
