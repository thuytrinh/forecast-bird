package thuytrinh.forecastbird.util;

import android.util.Log;

import javax.inject.Inject;

import rx.functions.Action1;
import thuytrinh.forecastbird.BuildConfig;

public class ErrorHandlerFactory {
  @Inject ErrorHandlerFactory() {}

  /**
   * @return An error handler that just prints the error out for the sake of debugging.
   * We can use this to catch some well-known errors like network error.
   */
  public Action1<Throwable> logError() {
    return error -> {
      if (BuildConfig.DEBUG) {
        Log.e(ErrorHandlerFactory.class.getSimpleName(), error.getMessage(), error);
      }
    };
  }

  /**
   * @return An error handler that if debuggable,
   * it just simply prints the error out like {@link #logError()} does.
   * Otherwise, it'll upload that error to a sort of crash report service
   * (e.g. Fabric) for further investigation.
   * We may need to use this to catch some errors that we may not be aware of.
   */
  public Action1<Throwable> trackError() {
    return error -> {
      if (BuildConfig.DEBUG) {
        Log.e(ErrorHandlerFactory.class.getSimpleName(), error.getMessage(), error);
      } else {
        // TODO: Upload error to a crash report service for further investigation.
      }
    };
  }
}