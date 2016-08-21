package thuytrinh.forecastbird.forecast;

import dagger.Subcomponent;
import thuytrinh.forecastbird.util.SubScope;

@SubScope
@Subcomponent(modules = ForecastModule.class)
public interface ForecastComponent {
  void inject(ForecastFragment fragment);
}