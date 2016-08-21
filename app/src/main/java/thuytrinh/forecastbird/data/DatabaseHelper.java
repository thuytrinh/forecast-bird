package thuytrinh.forecastbird.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;
import rx.Observable;
import skedgo.sqlite.Cursors;
import thuytrinh.forecastbird.api.QueryResponse;

import static thuytrinh.forecastbird.data.QueryResponseContract.TABLE_QUERY_RESPONSES;

@Singleton
public class DatabaseHelper extends SQLiteOpenHelper {
  private static final int VERSION = 1;
  private final Lazy<QueryResponseMapper> queryResponseMapperLazy;

  @Inject DatabaseHelper(
      Context context,
      Lazy<QueryResponseMapper> queryResponseMapperLazy) {
    super(context, "weather.db", null, VERSION);
    this.queryResponseMapperLazy = queryResponseMapperLazy;
  }

  @Override public void onCreate(SQLiteDatabase db) {
    // Create the table and its unique index.
    TABLE_QUERY_RESPONSES.create(db);
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

  Observable<QueryResponse> saveQueryResponseAsync(QueryResponse response) {
    return Observable
        .fromCallable(() -> {
          final SQLiteDatabase database = getWritableDatabase();
          database.insertWithOnConflict(
              TABLE_QUERY_RESPONSES.getName(), null,
              queryResponseMapperLazy.get().asContentValues(response),
              SQLiteDatabase.CONFLICT_REPLACE
          );
          return response;
        });
  }

  Observable<QueryResponse> loadQueryResponseAsync() {
    return Observable
        .fromCallable(() -> {
          final SQLiteDatabase database = getReadableDatabase();
          return database.query(TABLE_QUERY_RESPONSES.getName(), null, null, null, null, null, null);
        })
        .flatMap(Cursors.flattenCursor())
        .map(queryResponseMapperLazy.get()::asQueryResponse);
  }
}