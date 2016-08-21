package thuytrinh.forecastbird.binding;

import android.databinding.BindingConversion;
import android.databinding.ObservableBoolean;
import android.view.View;

/**
 * Factory for binding conversions.
 */
public final class Converters {
  private Converters() {}

  /**
   * Binds a boolean value into {@link View#setVisibility(int)}.
   * When the value turns false, the view is gone.
   * Otherwise, the view becomes visible.
   * <p>
   * A sample is like `android:visibility="@{viewModel.isBusy}` and
   * `isBusy` can be an {@link ObservableBoolean}.
   */
  @BindingConversion
  public static int convertBooleanToViewVisibility(boolean value) {
    return value ? View.VISIBLE : View.GONE;
  }
}