package thuytrinh.forecastbird.data;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import thuytrinh.forecastbird.BuildConfig;
import thuytrinh.forecastbird.api.Channel;
import thuytrinh.forecastbird.api.ChannelItem;
import thuytrinh.forecastbird.api.ForecastApi;
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

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ForecastRepositoryTest {
  @Rule public MockitoRule rule = MockitoJUnit.rule();
  @Mock ForecastApi forecastApi;
  @Mock DatabaseHelper databaseHelper;
  private ForecastRepository repository;

  @Before public void before() {
    repository = new ForecastRepository(
        () -> forecastApi,
        () -> databaseHelper,
        Schedulers.immediate()
    );
  }

  /**
   * In this test, we expect the repository will request data from network and
   * save that data into database later on. After saving, it should also post a notification
   * to let any observers know that persistent forecast data has changed.
   */
  @Test public void fetchAndSaveForecastSuccessfully() {
    final String query = "select * from weather.forecast where woeid " +
        "in (select woeid from geo.places(1) where text=\"amsterdam\")";

    // Simulate what sort of data the api would return.
    final QueryResponse response = createExpectedQueryResponse();
    when(forecastApi.queryAsync(eq(query)))
        .thenReturn(Observable.just(response));

    // Simulate how the database help would react to given data.
    when(databaseHelper.saveQueryResponseAsync(eq(response)))
        .thenReturn(Observable.just(response));

    // Observe any database change.
    final TestSubscriber<Void> changeSubscriber = new TestSubscriber<>();
    repository.onChange().subscribe(changeSubscriber);

    final TestSubscriber<QueryResponse> subscriber = new TestSubscriber<>();
    repository.fetchForecastAsync().subscribe(subscriber);

    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();

    verify(forecastApi).queryAsync(eq(query));
    verify(databaseHelper).saveQueryResponseAsync(eq(response));
    changeSubscriber.assertValue(null);
  }

  @Test public void loadForecastSuccessfully() {
    final QueryResponse response = createExpectedQueryResponse();
    when(databaseHelper.loadQueryResponseAsync())
        .thenReturn(Observable.just(response));

    final TestSubscriber<QueryResponse> subscriber = new TestSubscriber<>();
    repository.loadQueryResponseAsync().subscribe(subscriber);

    subscriber.awaitTerminalEvent();
    subscriber.assertNoErrors();
    subscriber.assertValue(response);
    verify(databaseHelper).loadQueryResponseAsync();
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