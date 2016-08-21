package thuytrinh.forecastbird.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;

import javax.inject.Inject;

import thuytrinh.forecastbird.api.QueryResponse;

import static thuytrinh.forecastbird.data.QueryResponseContract.FIELD_CITY;
import static thuytrinh.forecastbird.data.QueryResponseContract.FIELD_JSON;

public class QueryResponseMapper {
  private final Gson gson;

  @Inject QueryResponseMapper(Gson gson) {
    this.gson = gson;
  }

  QueryResponse asQueryResponse(Cursor cursor) {
    final String queryResponseJson = cursor.getString(cursor.getColumnIndex(FIELD_JSON.getName()));
    return gson.fromJson(queryResponseJson, QueryResponse.class);
  }

  ContentValues asContentValues(QueryResponse response) {
    final ContentValues values = new ContentValues(2);
    values.put(FIELD_CITY.getName(), "");
    values.put(FIELD_JSON.getName(), gson.toJson(response, QueryResponse.class));
    return values;
  }
}