package thuytrinh.forecastbird;

import javax.inject.Singleton;

import dagger.Component;
import thuytrinh.forecastbird.api.ApiModule;
import thuytrinh.forecastbird.forecast.ForecastComponent;

@Singleton
@Component(modules = {ApiModule.class, AppModule.class})
public interface AppComponent {
  ForecastComponent forecastComponent();
}