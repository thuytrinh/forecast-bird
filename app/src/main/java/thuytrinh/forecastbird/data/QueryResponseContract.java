package thuytrinh.forecastbird.data;

import skedgo.sqlite.DatabaseField;
import skedgo.sqlite.DatabaseTable;
import skedgo.sqlite.UniqueIndices;
import thuytrinh.forecastbird.api.QueryResponse;

interface QueryResponseContract {
  DatabaseField FIELD_ID = new DatabaseField("_id", "INTEGER", "PRIMARY KEY AUTOINCREMENT");

  /**
   * This is just a placeholder for now in case we would like to
   * support showing forecast for multiple cities in the future.
   */
  DatabaseField FIELD_CITY = new DatabaseField("city", "TEXT");

  /**
   * A json string that denotes {@link QueryResponse}.
   */
  DatabaseField FIELD_JSON = new DatabaseField("json", "TEXT");
  String NAME_QUERY_RESPONSES = "query_responses";
  DatabaseTable TABLE_QUERY_RESPONSES = new DatabaseTable(
      NAME_QUERY_RESPONSES,
      new DatabaseField[] {FIELD_ID, FIELD_CITY, FIELD_JSON},

      // City is unique.
      UniqueIndices.of(NAME_QUERY_RESPONSES, FIELD_CITY)
  );
}