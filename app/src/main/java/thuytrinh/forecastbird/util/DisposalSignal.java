package thuytrinh.forecastbird.util;

import rx.Observable;

public interface DisposalSignal {
  Observable<Void> asObservable();
  void dispatch();
}