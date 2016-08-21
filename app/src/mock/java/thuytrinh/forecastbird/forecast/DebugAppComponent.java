package thuytrinh.forecastbird.forecast;

import android.support.annotation.VisibleForTesting;

import javax.inject.Singleton;

import dagger.Component;
import thuytrinh.forecastbird.AppModule;
import thuytrinh.forecastbird.api.ApiModule;
import thuytrinh.forecastbird.data.DatabaseHelper;

/**
 * Exposes an access to {@link DatabaseHelper} to perform some functional tests on it.
 */
@Singleton
@Component(modules = {ApiModule.class, AppModule.class})
public interface DebugAppComponent {
  @VisibleForTesting DatabaseHelper databaseHelper();
}