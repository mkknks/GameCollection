package com.gamecollection.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.gamecollection.data.entity.GameMasterEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class GameMasterDao_Impl implements GameMasterDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<GameMasterEntity> __insertionAdapterOfGameMasterEntity;

  public GameMasterDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfGameMasterEntity = new EntityInsertionAdapter<GameMasterEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `game_master` (`id`,`title`,`platform`,`publisher`,`releaseYear`,`janCode`,`isUserAdded`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GameMasterEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        if (entity.getPlatform() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getPlatform());
        }
        if (entity.getPublisher() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getPublisher());
        }
        if (entity.getReleaseYear() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getReleaseYear());
        }
        if (entity.getJanCode() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getJanCode());
        }
        final int _tmp = entity.isUserAdded() ? 1 : 0;
        statement.bindLong(7, _tmp);
      }
    };
  }

  @Override
  public Object insert(final GameMasterEntity gameMaster,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfGameMasterEntity.insertAndReturnId(gameMaster);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<GameMasterEntity>> observeAll() {
    final String _sql = "SELECT * FROM game_master ORDER BY title ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"game_master"}, new Callable<List<GameMasterEntity>>() {
      @Override
      @NonNull
      public List<GameMasterEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfPlatform = CursorUtil.getColumnIndexOrThrow(_cursor, "platform");
          final int _cursorIndexOfPublisher = CursorUtil.getColumnIndexOrThrow(_cursor, "publisher");
          final int _cursorIndexOfReleaseYear = CursorUtil.getColumnIndexOrThrow(_cursor, "releaseYear");
          final int _cursorIndexOfJanCode = CursorUtil.getColumnIndexOrThrow(_cursor, "janCode");
          final int _cursorIndexOfIsUserAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "isUserAdded");
          final List<GameMasterEntity> _result = new ArrayList<GameMasterEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GameMasterEntity _item;
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
            final String _tmpJanCode;
            if (_cursor.isNull(_cursorIndexOfJanCode)) {
              _tmpJanCode = null;
            } else {
              _tmpJanCode = _cursor.getString(_cursorIndexOfJanCode);
            }
            final boolean _tmpIsUserAdded;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsUserAdded);
            _tmpIsUserAdded = _tmp != 0;
            _item = new GameMasterEntity(_tmpId,_tmpTitle,_tmpPlatform,_tmpPublisher,_tmpReleaseYear,_tmpJanCode,_tmpIsUserAdded);
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
  public Flow<GameMasterEntity> observeById(final long id) {
    final String _sql = "SELECT * FROM game_master WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"game_master"}, new Callable<GameMasterEntity>() {
      @Override
      @Nullable
      public GameMasterEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfPlatform = CursorUtil.getColumnIndexOrThrow(_cursor, "platform");
          final int _cursorIndexOfPublisher = CursorUtil.getColumnIndexOrThrow(_cursor, "publisher");
          final int _cursorIndexOfReleaseYear = CursorUtil.getColumnIndexOrThrow(_cursor, "releaseYear");
          final int _cursorIndexOfJanCode = CursorUtil.getColumnIndexOrThrow(_cursor, "janCode");
          final int _cursorIndexOfIsUserAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "isUserAdded");
          final GameMasterEntity _result;
          if (_cursor.moveToFirst()) {
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
            final String _tmpJanCode;
            if (_cursor.isNull(_cursorIndexOfJanCode)) {
              _tmpJanCode = null;
            } else {
              _tmpJanCode = _cursor.getString(_cursorIndexOfJanCode);
            }
            final boolean _tmpIsUserAdded;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsUserAdded);
            _tmpIsUserAdded = _tmp != 0;
            _result = new GameMasterEntity(_tmpId,_tmpTitle,_tmpPlatform,_tmpPublisher,_tmpReleaseYear,_tmpJanCode,_tmpIsUserAdded);
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
  public Flow<List<GameMasterEntity>> searchByTitle(final String query) {
    final String _sql = "SELECT * FROM game_master WHERE title LIKE '%' || ? || '%' ORDER BY title ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"game_master"}, new Callable<List<GameMasterEntity>>() {
      @Override
      @NonNull
      public List<GameMasterEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfPlatform = CursorUtil.getColumnIndexOrThrow(_cursor, "platform");
          final int _cursorIndexOfPublisher = CursorUtil.getColumnIndexOrThrow(_cursor, "publisher");
          final int _cursorIndexOfReleaseYear = CursorUtil.getColumnIndexOrThrow(_cursor, "releaseYear");
          final int _cursorIndexOfJanCode = CursorUtil.getColumnIndexOrThrow(_cursor, "janCode");
          final int _cursorIndexOfIsUserAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "isUserAdded");
          final List<GameMasterEntity> _result = new ArrayList<GameMasterEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GameMasterEntity _item;
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
            final String _tmpJanCode;
            if (_cursor.isNull(_cursorIndexOfJanCode)) {
              _tmpJanCode = null;
            } else {
              _tmpJanCode = _cursor.getString(_cursorIndexOfJanCode);
            }
            final boolean _tmpIsUserAdded;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsUserAdded);
            _tmpIsUserAdded = _tmp != 0;
            _item = new GameMasterEntity(_tmpId,_tmpTitle,_tmpPlatform,_tmpPublisher,_tmpReleaseYear,_tmpJanCode,_tmpIsUserAdded);
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
  public Object findByJanCode(final String janCode,
      final Continuation<? super List<GameMasterEntity>> $completion) {
    final String _sql = "SELECT * FROM game_master WHERE janCode = ? ORDER BY title ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, janCode);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<GameMasterEntity>>() {
      @Override
      @NonNull
      public List<GameMasterEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfPlatform = CursorUtil.getColumnIndexOrThrow(_cursor, "platform");
          final int _cursorIndexOfPublisher = CursorUtil.getColumnIndexOrThrow(_cursor, "publisher");
          final int _cursorIndexOfReleaseYear = CursorUtil.getColumnIndexOrThrow(_cursor, "releaseYear");
          final int _cursorIndexOfJanCode = CursorUtil.getColumnIndexOrThrow(_cursor, "janCode");
          final int _cursorIndexOfIsUserAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "isUserAdded");
          final List<GameMasterEntity> _result = new ArrayList<GameMasterEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GameMasterEntity _item;
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
            final String _tmpJanCode;
            if (_cursor.isNull(_cursorIndexOfJanCode)) {
              _tmpJanCode = null;
            } else {
              _tmpJanCode = _cursor.getString(_cursorIndexOfJanCode);
            }
            final boolean _tmpIsUserAdded;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsUserAdded);
            _tmpIsUserAdded = _tmp != 0;
            _item = new GameMasterEntity(_tmpId,_tmpTitle,_tmpPlatform,_tmpPublisher,_tmpReleaseYear,_tmpJanCode,_tmpIsUserAdded);
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
  public Object findByTitleAndPlatform(final String title, final String platform,
      final Continuation<? super GameMasterEntity> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM game_master\n"
            + "        WHERE title = ? AND IFNULL(platform, '') = IFNULL(?, '')\n"
            + "        LIMIT 1\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, title);
    _argIndex = 2;
    if (platform == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, platform);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<GameMasterEntity>() {
      @Override
      @Nullable
      public GameMasterEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfPlatform = CursorUtil.getColumnIndexOrThrow(_cursor, "platform");
          final int _cursorIndexOfPublisher = CursorUtil.getColumnIndexOrThrow(_cursor, "publisher");
          final int _cursorIndexOfReleaseYear = CursorUtil.getColumnIndexOrThrow(_cursor, "releaseYear");
          final int _cursorIndexOfJanCode = CursorUtil.getColumnIndexOrThrow(_cursor, "janCode");
          final int _cursorIndexOfIsUserAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "isUserAdded");
          final GameMasterEntity _result;
          if (_cursor.moveToFirst()) {
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
            final String _tmpJanCode;
            if (_cursor.isNull(_cursorIndexOfJanCode)) {
              _tmpJanCode = null;
            } else {
              _tmpJanCode = _cursor.getString(_cursorIndexOfJanCode);
            }
            final boolean _tmpIsUserAdded;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsUserAdded);
            _tmpIsUserAdded = _tmp != 0;
            _result = new GameMasterEntity(_tmpId,_tmpTitle,_tmpPlatform,_tmpPublisher,_tmpReleaseYear,_tmpJanCode,_tmpIsUserAdded);
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
  public Object getById(final long id, final Continuation<? super GameMasterEntity> $completion) {
    final String _sql = "SELECT * FROM game_master WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<GameMasterEntity>() {
      @Override
      @Nullable
      public GameMasterEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfPlatform = CursorUtil.getColumnIndexOrThrow(_cursor, "platform");
          final int _cursorIndexOfPublisher = CursorUtil.getColumnIndexOrThrow(_cursor, "publisher");
          final int _cursorIndexOfReleaseYear = CursorUtil.getColumnIndexOrThrow(_cursor, "releaseYear");
          final int _cursorIndexOfJanCode = CursorUtil.getColumnIndexOrThrow(_cursor, "janCode");
          final int _cursorIndexOfIsUserAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "isUserAdded");
          final GameMasterEntity _result;
          if (_cursor.moveToFirst()) {
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
            final String _tmpJanCode;
            if (_cursor.isNull(_cursorIndexOfJanCode)) {
              _tmpJanCode = null;
            } else {
              _tmpJanCode = _cursor.getString(_cursorIndexOfJanCode);
            }
            final boolean _tmpIsUserAdded;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsUserAdded);
            _tmpIsUserAdded = _tmp != 0;
            _result = new GameMasterEntity(_tmpId,_tmpTitle,_tmpPlatform,_tmpPublisher,_tmpReleaseYear,_tmpJanCode,_tmpIsUserAdded);
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
}
