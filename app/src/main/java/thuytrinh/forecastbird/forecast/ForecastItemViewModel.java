package thuytrinh.forecastbird.forecast;

import android.databinding.ObservableField;

import thuytrinh.forecastbird.api.ForecastItem;

/**
 * Represents a forecast item in the forecast list.
 * This is like a facade between {@link ForecastItem} and its corresponding view,
 * where some values of {@link ForecastItem} will be transformed before showing on UI.
 * For example, {@link ForecastItem#day()} and {@link ForecastItem#date()} can be combined and
 * mapped into correct localization. Or in the future, {@link #highestTemperature()} can plus
 * a "C" or "F" to specify unit.
 */
public interface ForecastItemViewModel {
  /**
   * @return A combination of {@link ForecastItem#day()} and {@link ForecastItem#date()}.
   */
  ObservableField<String> date();
  ObservableField<String> text();
  ObservableField<String> lowestTemperature();
  ObservableField<String> highestTemperature();

  /**
   * Invoking this will change {@link #date()}, {@link #text()},
   * {@link #lowestTemperature()}, and {@link #highestTemperature()}
   * accordingly to reflect the forecast item on UI.
   */
  void setForecastItem(ForecastItem forecastItem);
}