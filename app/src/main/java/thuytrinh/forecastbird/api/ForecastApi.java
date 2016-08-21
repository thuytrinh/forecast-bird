package thuytrinh.forecastbird.api;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface ForecastApi {
  /**
   * @param query A sample is like 'select * from weather.forecast where woeid in (select woeid from geo.places(1) where text="amsterdam")'.
   */
  @GET("v1/public/yql?format=json&env=store://datatables.org/alltableswithkeys")
  Observable<QueryResponse> queryAsync(@Query("q") String query);
}