package thuytrinh.forecastbird.forecast;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import thuytrinh.forecastbird.util.DisposalSignal;
import thuytrinh.forecastbird.util.DisposalSignals;

final class MockForecastViewModel implements ForecastViewModel {
  private final ObservableList<ForecastItemViewModel> forecastItemViewModels = new ObservableArrayList<>();
  private final ObservableBoolean isBusy = new ObservableBoolean();
  private final SwipeRefreshLayout.OnRefreshListener onRefreshListener;
  private final AtomicInteger refreshCounter = new AtomicInteger();

  MockForecastViewModel() {
    forecastItemViewModels.addAll(createInitialItems());
    onRefreshListener = () -> {
      isBusy.set(true);
      forecastItemViewModels.clear();

      // We only show non-empty forecast for the first refresh.
      // For the later refreshes, we show empty forecast.
      if (refreshCounter.incrementAndGet() == 1) {
        forecastItemViewModels.addAll(createLatestItems());
      }
      isBusy.set(false);
    };
  }

  @Override public ObservableList<ForecastItemViewModel> forecastItemViewModels() {
    return forecastItemViewModels;
  }

  @Override public ObservableBoolean isBusy() {
    return isBusy;
  }

  @Override public DisposalSignal disposalSignal() {
    return DisposalSignals.create();
  }

  @Override public SwipeRefreshLayout.OnRefreshListener onRefreshListener() {
    return onRefreshListener;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {}

  private List<MockForecastItemViewModel> createLatestItems() {
    final MockForecastItemViewModel a = new MockForecastItemViewModel();
    a.date().set("Monday, August 22, 2016");
    a.highestTemperature().set("21");
    a.lowestTemperature().set("11");
    a.text().set("Sunny");

    final MockForecastItemViewModel b = new MockForecastItemViewModel();
    b.date().set("Tuesday, August 23, 2016");
    b.highestTemperature().set("22");
    b.lowestTemperature().set("12");
    b.text().set("Showers");

    final MockForecastItemViewModel c = new MockForecastItemViewModel();
    c.date().set("Wednesday, August 24, 2016");
    c.highestTemperature().set("23");
    c.lowestTemperature().set("13");
    c.text().set("Scattered Showers");

    return Arrays.asList(a, b, c);
  }

  private List<MockForecastItemViewModel> createInitialItems() {
    final MockForecastItemViewModel a = new MockForecastItemViewModel();
    a.date().set("Friday, August 19, 2016");
    a.highestTemperature().set("24");
    a.lowestTemperature().set("17");
    a.text().set("Sunny");

    final MockForecastItemViewModel b = new MockForecastItemViewModel();
    b.date().set("Saturday, August 20, 2016");
    b.highestTemperature().set("25");
    b.lowestTemperature().set("16");
    b.text().set("Showers");

    final MockForecastItemViewModel c = new MockForecastItemViewModel();
    c.date().set("Sunday, August 21, 2016");
    c.highestTemperature().set("27");
    c.lowestTemperature().set("19");
    c.text().set("Scattered Showers");

    return Arrays.asList(a, b, c);
  }
}