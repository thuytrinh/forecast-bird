package thuytrinh.forecastbird.forecast;

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
import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;
import thuytrinh.forecastbird.BuildConfig;
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
import thuytrinh.forecastbird.data.ForecastRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ForecastLoaderTest {
  @Rule public MockitoRule rule = MockitoJUnit.rule();
  @Mock ForecastRepository forecastRepository;
  private ForecastLoader loader;

  @Before public void before() {
    loader = new ForecastLoader(() -> forecastRepository);
  }

  @Test public void loadAndExtractForecast() {
    final List<ForecastItem> initialForecast = createInitialForecast();
    final QueryResponse response = createQueryResponse(initialForecast);
    when(forecastRepository.loadQueryResponseAsync())
        .thenReturn(Observable.just(response));

    final PublishSubject<Void> onChange = PublishSubject.create();
    when(forecastRepository.onChange())
        .thenReturn(onChange.asObservable());

    final TestSubscriber<List<ForecastItem>> subscriber = new TestSubscriber<>();
    loader.call().subscribe(subscriber);

    subscriber.assertNoErrors();
    subscriber.assertValue(initialForecast);
  }

  @Test public void loadAgainWhenGettingChangeSignal() {
    final AtomicInteger requestCounter = new AtomicInteger();
    when(forecastRepository.loadQueryResponseAsync())
        .thenReturn(Observable.fromCallable(() -> {
          if (requestCounter.incrementAndGet() < 2) {
            return createQueryResponse(createInitialForecast());
          } else {
            return createQueryResponse(createLatestForecast());
          }
        }));

    final PublishSubject<Void> onChange = PublishSubject.create();
    when(forecastRepository.onChange())
        .thenReturn(onChange.asObservable());

    final TestSubscriber<List<ForecastItem>> subscriber = new TestSubscriber<>();
    loader.call().subscribe(subscriber);

    subscriber.assertNoErrors();
    subscriber.assertValue(createInitialForecast());

    // Post a change notification.
    onChange.onNext(null);

    subscriber.assertNoErrors();
    subscriber.assertValues(createInitialForecast(), createLatestForecast());
  }

  @Test public void stopObservingChangeSignal() {
    when(forecastRepository.loadQueryResponseAsync())
        .thenReturn(Observable.empty());

    final PublishSubject<Void> onChange = PublishSubject.create();
    when(forecastRepository.onChange())
        .thenReturn(onChange.asObservable());

    final Subscription subscription = loader.call().subscribe();
    assertThat(onChange.hasObservers()).isTrue();
    subscription.unsubscribe();
    assertThat(onChange.hasObservers()).isFalse();
  }

  private QueryResponse createQueryResponse(List<ForecastItem> forecast) {
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

  private List<ForecastItem> createInitialForecast() {
    return Arrays.asList(
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
  }

  private List<ForecastItem> createLatestForecast() {
    return Arrays.asList(
        ImmutableForecastItem.builder()
            .code("32")
            .date("21 Aug 2016")
            .day("Sun")
            .high("34")
            .low("23")
            .text("Scattered Showers")
            .build(),
        ImmutableForecastItem.builder()
            .code("11")
            .date("22 Aug 2016")
            .day("Mon")
            .high("21")
            .low("11")
            .text("Showers")
            .build()
    );
  }
}