package thuytrinh.forecastbird.forecast;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import thuytrinh.forecastbird.util.DisposalSignal;

public interface ForecastViewModel {
  ObservableList<ForecastItemViewModel> forecastItemViewModels();
  ObservableBoolean isBusy();
  DisposalSignal disposalSignal();
  SwipeRefreshLayout.OnRefreshListener onRefreshListener();
  void onCreate(@Nullable Bundle savedInstanceState);
}