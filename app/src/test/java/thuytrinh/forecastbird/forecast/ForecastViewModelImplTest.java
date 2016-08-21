package thuytrinh.forecastbird.forecast;

import android.os.Bundle;

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
import rx.functions.Action1;
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
import thuytrinh.forecastbird.util.ErrorHandlerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ForecastViewModelImplTest {
  @Rule public MockitoRule rule = MockitoJUnit.rule();
  @Mock Action1<Throwable> errorLogger;
  @Mock Action1<Throwable> errorTracker;
  @Mock ForecastRepository forecastRepository;
  @Mock ErrorHandlerFactory errorHandlerFactory;
  @Mock ForecastLoader forecastLoader;
  private ForecastViewModelImpl viewModel;

  @Before public void before() {
    viewModel = new ForecastViewModelImpl(
        forecastLoader,
        () -> forecastRepository,
        () -> errorHandlerFactory,
        () -> mock(ForecastItemViewModel.class)
    );

    when(errorHandlerFactory.logError()).thenReturn(errorLogger);
    when(errorHandlerFactory.trackError()).thenReturn(errorTracker);
  }

  @Test public void initiallyForecastIsEmpty() {
    assertThat(viewModel.forecastItemViewModels()).isEmpty();
  }

  @Test public void initiallyIsNotBusy() {
    assertThat(viewModel.isBusy().get()).isFalse();
  }

  @Test public void stopReFetchingBeforeBeingDisposed() {
    final PublishSubject<QueryResponse> fetcher = PublishSubject.create();
    when(forecastRepository.fetchForecastAsync())
        .thenReturn(fetcher);

    // Simulate a pull-2-refresh.
    viewModel.onRefreshListener().onRefresh();
    assertThat(fetcher.hasObservers()).isTrue();

    // Post disposal signal.
    viewModel.disposalSignal().dispatch();
    assertThat(fetcher.hasObservers()).isFalse();
  }

  @Test public void fetchForecastAgainAfterPull2Refresh_success() {
    final PublishSubject<QueryResponse> fetcher = PublishSubject.create();
    when(forecastRepository.fetchForecastAsync())
        .thenReturn(fetcher);

    // Simulate a pull-2-refresh.
    viewModel.onRefreshListener().onRefresh();

    verify(forecastRepository).fetchForecastAsync();
    verify(errorHandlerFactory).logError();
    assertThat(fetcher.hasObservers()).isTrue();
  }

  @Test public void fetchForecastAgainAfterPull2Refresh_failure() {
    when(forecastRepository.fetchForecastAsync())
        .thenReturn(Observable.error(new Throwable("Some error")));

    // Simulate a pull-2-refresh.
    viewModel.onRefreshListener().onRefresh();

    // Make sure the error was already handled via the logger.
    verify(errorLogger).call(any(Throwable.class));
  }

  @Test public void stopFetchingForecastBeforeBeingDisposed() {
    final PublishSubject<QueryResponse> fetcher = PublishSubject.create();
    when(forecastRepository.fetchForecastAsync())
        .thenReturn(fetcher);
    when(forecastLoader.call()).thenReturn(Observable.empty());

    // onCreate() is the point we start fetching forecast.
    // 'null' indicates that when app is back from sleep,
    // we wouldn't fetch forecast.
    viewModel.onCreate(null);
    assertThat(fetcher.hasObservers()).isTrue();

    // Post disposal signal.
    viewModel.disposalSignal().dispatch();
    assertThat(fetcher.hasObservers()).isFalse();
  }

  @Test public void beginFetchingForecastWhenAppIsNewlyCreated_success() {
    final PublishSubject<QueryResponse> fetcher = PublishSubject.create();
    when(forecastRepository.fetchForecastAsync())
        .thenReturn(fetcher);
    when(forecastLoader.call()).thenReturn(Observable.empty());

    // onCreate() is the point we start fetching forecast.
    // 'null' indicates that when app is back from sleep,
    // we wouldn't fetch forecast.
    viewModel.onCreate(null);

    verify(forecastRepository).fetchForecastAsync();
    verify(errorHandlerFactory).logError();
    assertThat(fetcher.hasObservers()).isTrue();
  }

  @Test public void beginFetchingForecastWhenAppIsNewlyCreated_failure() {
    when(forecastRepository.fetchForecastAsync())
        .thenReturn(Observable.error(new Throwable("Some error")));
    when(forecastLoader.call()).thenReturn(Observable.empty());

    // onCreate() is the point we start fetching forecast.
    // 'null' indicates that when app is back from sleep,
    // we wouldn't fetch forecast.
    viewModel.onCreate(null);

    // Make sure the error was already handled via the logger.
    verify(errorLogger).call(any(Throwable.class));
  }

  @Test public void doNotFetchForecastWhenAppIsBackFromSleep() {
    when(forecastRepository.fetchForecastAsync())
        .thenReturn(Observable.empty());
    when(forecastLoader.call()).thenReturn(Observable.empty());

    viewModel.onCreate(new Bundle());

    verify(forecastRepository, never()).fetchForecastAsync();
  }

  @Test public void loadForecast() {
    when(forecastLoader.call())
        .thenReturn(Observable.just(createExpectedForecast()));

    viewModel.onCreate(new Bundle());
    assertThat(viewModel.forecastItemViewModels())
        .hasSize(3)
        .doesNotContainNull()
        .doesNotHaveDuplicates();
  }

  @Test public void stopLoadingBeforeBeingDisposed() {
    final PublishSubject<List<ForecastItem>> loader = PublishSubject.create();
    when(forecastLoader.call()).thenReturn(loader);

    viewModel.onCreate(new Bundle());
    assertThat(loader.hasObservers()).isTrue();

    viewModel.disposalSignal().dispatch();
    assertThat(loader.hasObservers()).isFalse();
  }

  @Test public void signalBusyStatusOfReFetchingCorrectly_success() {
    final PublishSubject<QueryResponse> fetcher = PublishSubject.create();
    when(forecastRepository.fetchForecastAsync())
        .thenReturn(fetcher);

    // Simulate a pull-2-refresh.
    // Begin fetching.
    viewModel.onRefreshListener().onRefresh();
    assertThat(viewModel.isBusy().get()).isTrue();

    // Simulate getting forecast.
    fetcher.onNext(createExpectedQueryResponse());
    assertThat(viewModel.isBusy().get()).isTrue();

    // Done fetching.
    fetcher.onCompleted();
    assertThat(viewModel.isBusy().get()).isFalse();
  }

  @Test public void signalBusyStatusOfReFetchingCorrectly_failure() {
    final PublishSubject<QueryResponse> fetcher = PublishSubject.create();
    when(forecastRepository.fetchForecastAsync())
        .thenReturn(fetcher);

    // Simulate a pull-2-refresh.
    // Begin fetching.
    viewModel.onRefreshListener().onRefresh();
    assertThat(viewModel.isBusy().get()).isTrue();

    // Simulate hitting an error.
    fetcher.onError(new RuntimeException("Some error during fetching"));
    assertThat(viewModel.isBusy().get()).isFalse();
  }

  @Test public void signalBusyStatusOfFetchingCorrectly_success() {
    final PublishSubject<QueryResponse> fetcher = PublishSubject.create();
    when(forecastRepository.fetchForecastAsync())
        .thenReturn(fetcher);
    when(forecastLoader.call()).thenReturn(Observable.empty());

    // Begin fetching.
    viewModel.onCreate(null);
    assertThat(viewModel.isBusy().get()).isTrue();

    // Simulate hitting an error.
    fetcher.onError(new RuntimeException("Some error during fetching"));
    assertThat(viewModel.isBusy().get()).isFalse();
  }

  private QueryResponse createExpectedQueryResponse() {
    final List<ForecastItem> forecast = createExpectedForecast();
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

  private List<ForecastItem> createExpectedForecast() {
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
}