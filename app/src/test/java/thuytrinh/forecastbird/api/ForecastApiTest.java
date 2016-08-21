package thuytrinh.forecastbird.api;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import thuytrinh.forecastbird.BuildConfig;
import thuytrinh.mockwebserverrule.MockWebServerRule;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ForecastApiTest {
  @Rule public MockWebServerRule serverRule = new MockWebServerRule();
  private ForecastApi api;

  @Before public void before() {
    api = new Retrofit.Builder()
        .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.immediate()))
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(serverRule.server.url("/"))
        .build()
        .create(ForecastApi.class);
  }

  /**
   * Given a JSON file simulating network response, we'll assert
   * whether or not the api would be able to parse into expected data models.
   */
  @Test public void querySuccessfully() throws IOException {
    final MockResponse mockResponse = MockWebServerRule.createMockResponse("/forecast.json");
    serverRule.server.enqueue(mockResponse);

    final TestSubscriber<QueryResponse> subscriber = new TestSubscriber<>();
    api.queryAsync("").subscribe(subscriber);

    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertValue(createExpectedQueryResponse());
  }

  /**
   * @return Data model that depicts exactly json model in the 'forecast.json' file.
   */
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