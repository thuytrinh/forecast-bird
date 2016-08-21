package thuytrinh.forecastbird.util;

import rx.Observable;
import rx.subjects.PublishSubject;

public final class DisposalSignals {
  private DisposalSignals() {}

  public static DisposalSignal create() {
    return new DisposalSignalImpl();
  }

  private static class DisposalSignalImpl implements DisposalSignal {
    private final PublishSubject<Void> signal = PublishSubject.create();

    @Override public Observable<Void> asObservable() {
      return signal.asObservable();
    }

    @Override public void dispatch() {
      signal.onNext(null);
    }
  }
}