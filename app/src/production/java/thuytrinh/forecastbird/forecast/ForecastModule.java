package thuytrinh.forecastbird.forecast;

import dagger.Module;
import dagger.Provides;

@Module
public class ForecastModule {
  @Provides ForecastViewModel forecastViewModel(ForecastViewModelImpl viewModel) {
    return viewModel;
  }

  @Provides ForecastItemViewModel forecastItemViewModel(ForecastItemViewModelImpl viewModel) {
    return viewModel;
  }
}