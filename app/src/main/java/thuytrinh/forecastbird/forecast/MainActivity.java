package thuytrinh.forecastbird.forecast;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState == null) {
      getSupportFragmentManager()
          .beginTransaction()
          .add(android.R.id.content, new ForecastFragment())
          .commit();
    }
  }
}