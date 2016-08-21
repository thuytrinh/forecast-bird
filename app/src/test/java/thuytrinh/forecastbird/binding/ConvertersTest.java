package thuytrinh.forecastbird.binding;

import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import thuytrinh.forecastbird.BuildConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static thuytrinh.forecastbird.binding.Converters.convertBooleanToViewVisibility;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ConvertersTest {
  @Test public void visibilityIsGone() {
    assertThat(convertBooleanToViewVisibility(false)).isEqualTo(View.GONE);
  }

  @Test public void visibilityIsVisible() {
    assertThat(convertBooleanToViewVisibility(true)).isEqualTo(View.VISIBLE);
  }
}