package thuytrinh.forecastbird.forecast;

import android.databinding.ObservableField;

import thuytrinh.forecastbird.api.ForecastItem;

final class MockForecastItemViewModel implements ForecastItemViewModel {
  private final ObservableField<String> date = new ObservableField<>();
  private final ObservableField<String> text = new ObservableField<>();
  private final ObservableField<String> lowestTemperature = new ObservableField<>();
  private final ObservableField<String> highestTemperature = new ObservableField<>();

  @Override public ObservableField<String> date() {
    return date;
  }

  @Override public ObservableField<String> text() {
    return text;
  }

  @Override public ObservableField<String> lowestTemperature() {
    return lowestTemperature;
  }

  @Override public ObservableField<String> highestTemperature() {
    return highestTemperature;
  }

  @Override public void setForecastItem(ForecastItem forecastItem) {}
}