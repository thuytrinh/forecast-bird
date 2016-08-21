package thuytrinh.forecastbird.forecast;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import me.tatarka.bindingcollectionadapter.ItemView;
import thuytrinh.forecastbird.App;
import thuytrinh.forecastbird.BR;
import thuytrinh.forecastbird.R;
import thuytrinh.forecastbird.databinding.ForecastBinding;

public class ForecastFragment extends Fragment {
  @Inject ForecastViewModel viewModel;

  /**
   * @return A binding for what sort of layout
   * should be associated with a {@link ForecastItemViewModel}.
   */
  public static ItemView forecastItemView() {
    // 'viewModel' should be a var whose type is 'ForecastItemViewModel'.
    return ItemView.of(BR.viewModel, R.layout.forecast_item);
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Because a retained fragment won't die
    // (aka onDestroy() gets invoked) over screen rotation.
    setRetainInstance(true);
    App.component().forecastComponent().inject(this);

    // Delegate handling restoring states to the ViewModel side.
    viewModel.onCreate(savedInstanceState);
  }

  @Nullable @Override
  public View onCreateView(
      LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.forecast, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    final ForecastBinding binding = ForecastBinding.bind(view);
    binding.setViewModel(viewModel);
  }

  @Override public void onDestroy() {
    // Tell the viewModel to stop any asynchronous operations.
    viewModel.disposalSignal().dispatch();
    super.onDestroy();
  }
}