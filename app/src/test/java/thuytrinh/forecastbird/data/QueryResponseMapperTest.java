package thuytrinh.forecastbird.data;

import android.content.ContentValues;
import android.database.MatrixCursor;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import thuytrinh.forecastbird.BuildConfig;
import thuytrinh.forecastbird.api.ImmutableQueryResponse;
import thuytrinh.forecastbird.api.ImmutableQueryValue;
import thuytrinh.forecastbird.api.QueryResponse;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class QueryResponseMapperTest {
  private QueryResponseMapper mapper;
  private Gson gson;

  @Before public void before() {
    gson = new Gson();
    mapper = new QueryResponseMapper(gson);
  }

  @Test public void convertToContentValues() {
    final QueryResponse expectedResponse = ImmutableQueryResponse.builder()
        .query(ImmutableQueryValue.builder().build())
        .build();

    final ContentValues contentValues = mapper.asContentValues(expectedResponse);
    assertThat(contentValues.size()).isEqualTo(2);
    assertThat(contentValues.getAsString("city")).isEmpty();
    assertThat(contentValues.getAsString("json"))
        .isEqualTo(gson.toJson(expectedResponse, QueryResponse.class));
  }

  @Test public void convertToQueryResponse() {
    final QueryResponse expectedResponse = ImmutableQueryResponse.builder()
        .query(ImmutableQueryValue.builder().build())
        .build();

    final String queryResponseJson = gson.toJson(expectedResponse, QueryResponse.class);
    final MatrixCursor cursor = new MatrixCursor(new String[] {"_id", "city", "json"});
    cursor.addRow(new Object[] {1, "city A", queryResponseJson});
    cursor.moveToFirst();

    final QueryResponse actual = mapper.asQueryResponse(cursor);
    assertThat(actual).isEqualTo(expectedResponse);
  }
}