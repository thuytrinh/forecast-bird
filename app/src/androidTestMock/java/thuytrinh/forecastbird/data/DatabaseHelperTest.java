package thuytrinh.forecastbird.data;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import rx.observers.TestSubscriber;
import thuytrinh.forecastbird.AppModule;
import thuytrinh.forecastbird.api.Channel;
import thuytrinh.forecastbird.api.ChannelItem;
import thuytrinh.forecastbird.api.ForecastItem;
import thuytrinh.forecastbird.api.ImmutableChannel;
import thuytrinh.forecastbird.api.ImmutableChannelItem;
import thuytrinh.forecastbird.api.ImmutableForecastItem;
import thuytrinh.forecastbird.api.ImmutableQueryResponse;
import thuytrinh.forecastbird.api.ImmutableQueryResults;
import thuytrinh.forecastbird.api.ImmutableQueryValue;
import thuytrinh.forecastbird.api.QueryResponse;
import thuytrinh.forecastbird.api.QueryResults;
import thuytrinh.forecastbird.api.QueryValue;
import thuytrinh.forecastbird.forecast.DaggerDebugAppComponent;

@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {
  private DatabaseHelper databaseHelper;

  @Before public void before() {
    databaseHelper = DaggerDebugAppComponent.builder()
        .appModule(new AppModule(InstrumentationRegistry.getTargetContext()))
        .build()
        .databaseHelper();

    // Clear any previously-saved items to make tests less flaky.
    databaseHelper.getWritableDatabase().delete(
        QueryResponseContract.NAME_QUERY_RESPONSES,
        null, null
    );
  }

  /**
   * We expect that what we loaded is exactly what we saved previously.
   */
  @Test public void saveAndLoadQueryResponse() {
    final QueryResponse response = createExpectedQueryResponse();

    final TestSubscriber<QueryResponse> subscriber = new TestSubscriber<>();
    databaseHelper.saveQueryResponseAsync(response)
        .flatMap(x -> databaseHelper.loadQueryResponseAsync())
        .subscribe(subscriber);

    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertValue(response);
  }

  private QueryResponse createExpectedQueryResponse() {
    final List<ForecastItem> forecast = Arrays.asList(
        ImmutableForecastItem.builder()
            .code("32")
            .date("18 Aug 2016")
            .day("Thu")
            .high("74")
            .low("58")
            .text("Sunny")
            .build(),
        ImmutableForecastItem.builder()
            .code("11")
            .date("19 Aug 2016")
            .day("Fri")
            .high("75")
            .low("60")
            .text("Showers")
            .build(),
        ImmutableForecastItem.builder()
            .code("39")
            .date("20 Aug 2016")
            .day("Sat")
            .high("69")
            .low("61")
            .text("Scattered Showers")
            .build()
    );
    final ChannelItem item = ImmutableChannelItem.builder()
        .forecast(forecast)
        .build();
    final Channel channel = ImmutableChannel.builder()
        .item(item)
        .build();
    final QueryResults results = ImmutableQueryResults.builder()
        .channel(channel)
        .build();
    final QueryValue query = ImmutableQueryValue.builder()
        .results(results)
        .build();
    return ImmutableQueryResponse.builder()
        .query(query)
        .build();
  }
}