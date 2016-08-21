package thuytrinh.forecastbird.forecast;

import android.databinding.ObservableField;

import javax.inject.Inject;

import thuytrinh.forecastbird.api.ForecastItem;

final class ForecastItemViewModelImpl implements ForecastItemViewModel {
  private final ObservableField<String> date = new ObservableField<>();
  private final ObservableField<String> text = new ObservableField<>();
  private final ObservableField<String> lowestTemperature = new ObservableField<>();
  private final ObservableField<String> highestTemperature = new ObservableField<>();

  @Inject ForecastItemViewModelImpl() {}

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

  @Override public void setForecastItem(ForecastItem forecastItem) {
    if (forecastItem != null) {
      date.set(forecastItem.day() + ", " + forecastItem.date());
      text.set(forecastItem.text());
      lowestTemperature.set(forecastItem.low());
      highestTemperature.set(forecastItem.high());
    } else {
      date.set(null);
      text.set(null);
      lowestTemperature.set(null);
      highestTemperature.set(null);
    }
  }
}