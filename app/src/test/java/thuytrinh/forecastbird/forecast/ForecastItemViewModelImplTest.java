package thuytrinh.forecastbird.forecast;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import thuytrinh.forecastbird.BuildConfig;
import thuytrinh.forecastbird.api.ImmutableForecastItem;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ForecastItemViewModelImplTest {
  private ForecastItemViewModelImpl viewModel;

  @Before public void before() {
    viewModel = new ForecastItemViewModelImpl();
  }

  @Test public void computePropertiesFromForecastItem() {
    viewModel.setForecastItem(
        ImmutableForecastItem.builder()
            .code("Some code")
            .text("Some text")
            .day("Fri")
            .date("August 21, 2020")
            .high("23")
            .low("14")
            .build()
    );
    assertThat(viewModel.date().get()).isEqualTo("Fri, August 21, 2020");
    assertThat(viewModel.highestTemperature().get()).isEqualTo("23");
    assertThat(viewModel.lowestTemperature().get()).isEqualTo("14");
    assertThat(viewModel.text().get()).isEqualTo("Some text");
  }

  @Test public void resetPropertiesIfForecastItemIsNull() {
    viewModel.date().set("Fri, August 21, 2020");
    viewModel.text().set("Cloudy");
    viewModel.lowestTemperature().set("23");
    viewModel.highestTemperature().set("12");

    viewModel.setForecastItem(null);

    assertThat(viewModel.date().get()).isNull();
    assertThat(viewModel.highestTemperature().get()).isNull();
    assertThat(viewModel.lowestTemperature().get()).isNull();
    assertThat(viewModel.text().get()).isNull();
  }
}