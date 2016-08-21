package thuytrinh.forecastbird.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import rx.observers.TestSubscriber;
import thuytrinh.forecastbird.BuildConfig;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class DisposalSignalTest {
  @Test public void signal() {
    final DisposalSignal signal = DisposalSignals.create();

    final TestSubscriber<Void> subscriber = new TestSubscriber<>();
    signal.asObservable().subscribe(subscriber);

    signal.dispatch();
    subscriber.assertValue(null);
  }
}