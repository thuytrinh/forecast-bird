package thuytrinh.forecastbird.data;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Observable;
import rx.Scheduler;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import thuytrinh.forecastbird.api.ForecastApi;
import thuytrinh.forecastbird.api.QueryResponse;
import thuytrinh.forecastbird.util.SubScope;

@SubScope
public class ForecastRepository {
  private final Lazy<ForecastApi> apiLazy;
  private final Lazy<DatabaseHelper> databaseHelperLazy;
  private final Scheduler scheduler;
  private final Subject<Void, Void> changeSignal = PublishSubject.<Void>create().toSerialized();

  @Inject ForecastRepository(
      Lazy<ForecastApi> apiLazy,
      Lazy<DatabaseHelper> databaseHelperLazy,
      Scheduler scheduler) {
    this.apiLazy = apiLazy;
    this.databaseHelperLazy = databaseHelperLazy;
    this.scheduler = scheduler;
  }

  /**
   * @return An {@link Observable} to notify observers that database has changed.
   */
  public Observable<Void> onChange() {
    return changeSignal.asObservable();
  }

  public Observable<QueryResponse> fetchForecastAsync() {
    // A query to return forecast data for Amsterdam.
    final String query = "select * from weather.forecast where woeid " +
        "in (select woeid from geo.places(1) where text=\"amsterdam\")";
    return apiLazy.get()
        .queryAsync(query)
        // Save into database right after we successfully fetch from network.
        .flatMap(databaseHelperLazy.get()::saveQueryResponseAsync)
        // Notify observers that database has changed.
        .doOnNext(x -> changeSignal.onNext(null))
        .subscribeOn(scheduler);
  }

  public Observable<QueryResponse> loadQueryResponseAsync() {
    return databaseHelperLazy.get()
        .loadQueryResponseAsync()
        .subscribeOn(scheduler);
  }
}