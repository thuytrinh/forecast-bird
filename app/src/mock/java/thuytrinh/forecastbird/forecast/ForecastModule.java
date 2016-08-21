package thuytrinh.forecastbird.forecast;

import dagger.Module;
import dagger.Provides;

@Module
public class ForecastModule {
  @Provides ForecastViewModel forecastViewModel() {
    return new MockForecastViewModel();
  }
}