package thuytrinh.forecastbird.api;

import android.os.Parcel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import thuytrinh.forecastbird.BuildConfig;
import thuytrinh.forecastbird.Parcels;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ForecastItemTest {
  @Test public void shouldParcelCorrectly() {
    final ForecastItem expectedForecastItem = ImmutableForecastItem.builder()
        .code("Some code")
        .text("Some text")
        .day("Fri")
        .date("August 21, 2020")
        .high("23")
        .low("14")
        .build();

    final Parcel parcel = Parcels.parcel(expectedForecastItem);
    final ForecastItem actualForecastItem = ForecastItem.CREATOR.createFromParcel(parcel);
    assertThat(actualForecastItem).isEqualTo(expectedForecastItem);
  }
}