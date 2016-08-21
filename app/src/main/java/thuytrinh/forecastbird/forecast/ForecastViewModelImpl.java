package thuytrinh.forecastbird.forecast;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.Lazy;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Actions;
import thuytrinh.forecastbird.api.ForecastItem;
import thuytrinh.forecastbird.data.ForecastRepository;
import thuytrinh.forecastbird.util.DisposalSignal;
import thuytrinh.forecastbird.util.DisposalSignals;
import thuytrinh.forecastbird.util.ErrorHandlerFactory;

final class ForecastViewModelImpl implements ForecastViewModel {
  private final ObservableList<ForecastItemViewModel> forecastItemViewModels = new ObservableArrayList<>();
  private final ObservableBoolean isBusy = new ObservableBoolean();
  private final DisposalSignal disposalSignal = DisposalSignals.create();
  private final SwipeRefreshLayout.OnRefreshListener onRefreshListener;
  private final Lazy<ForecastRepository> forecastRepositoryLazy;
  private final Lazy<ErrorHandlerFactory> errorHandlerFactoryLazy;
  private final Provider<ForecastItemViewModel> forecastItemViewModelProvider;
  private final ForecastLoader forecastLoader;

  @Inject ForecastViewModelImpl(
      ForecastLoader forecastLoader,
      Lazy<ForecastRepository> forecastRepositoryLazy,
      Lazy<ErrorHandlerFactory> errorHandlerFactoryLazy,
      Provider<ForecastItemViewModel> forecastItemViewModelProvider) {
    this.forecastLoader = forecastLoader;
    this.forecastRepositoryLazy = forecastRepositoryLazy;
    this.errorHandlerFactoryLazy = errorHandlerFactoryLazy;
    this.forecastItemViewModelProvider = forecastItemViewModelProvider;

    // Re-fetch forecast after users do pull-2-refresh.
    onRefreshListener = this::fetchForecast;
  }

  @Override public ObservableList<ForecastItemViewModel> forecastItemViewModels() {
    return forecastItemViewModels;
  }

  @Override public ObservableBoolean isBusy() {
    return isBusy;
  }

  @Override public DisposalSignal disposalSignal() {
    return disposalSignal;
  }

  @Override public SwipeRefreshLayout.OnRefreshListener onRefreshListener() {
    return onRefreshListener;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    // When app is back from sleep, we won't fetch from network
    // but go with whatever we have on disk.
    if (savedInstanceState == null) {
      fetchForecast();
    }

    // Load forecast from disk.
    forecastLoader.call()
        .map(this::asForecastItemViewModels)
        // Cancel this async operation when the ViewModel is about to be disposed.
        .takeUntil(disposalSignal.asObservable())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::updateForecastItemViewModels, errorHandlerFactoryLazy.get().trackError());
  }

  private void fetchForecast() {
    forecastRepositoryLazy.get()
        .fetchForecastAsync()
        .doOnSubscribe(() -> isBusy.set(true))
        .doOnUnsubscribe(() -> isBusy.set(false))
        // Cancel this async operation when the ViewModel is about to be disposed.
        .takeUntil(disposalSignal.asObservable())
        .subscribe(Actions.empty(), errorHandlerFactoryLazy.get().logError());
  }

  private void updateForecastItemViewModels(List<ForecastItemViewModel> viewModels) {
    forecastItemViewModels.clear();
    forecastItemViewModels.addAll(viewModels);
  }

  private List<ForecastItemViewModel> asForecastItemViewModels(List<ForecastItem> forecastItems) {
    final List<ForecastItemViewModel> viewModels = new ArrayList<>(forecastItems.size());
    for (ForecastItem forecastItem : forecastItems) {
      final ForecastItemViewModel viewModel = forecastItemViewModelProvider.get();
      viewModel.setForecastItem(forecastItem);
      viewModels.add(viewModel);
    }
    return viewModels;
  }
}