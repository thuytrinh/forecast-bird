package thuytrinh.forecastbird;

import android.app.Application;

public class App extends Application {
  private static AppComponent component;

  public static AppComponent component() {
    return component;
  }

  @Override public void onCreate() {
    super.onCreate();
    component = DaggerAppComponent.builder()
        .appModule(new AppModule(this))
        .build();
  }
}