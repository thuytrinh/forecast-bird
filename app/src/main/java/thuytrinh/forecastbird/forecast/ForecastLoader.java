package thuytrinh.forecastbird.forecast;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Observable;
import rx.functions.Func0;
import thuytrinh.forecastbird.api.Channel;
import thuytrinh.forecastbird.api.ChannelItem;
import thuytrinh.forecastbird.api.ForecastItem;
import thuytrinh.forecastbird.api.QueryResults;
import thuytrinh.forecastbird.api.QueryValue;
import thuytrinh.forecastbird.data.ForecastRepository;

class ForecastLoader implements Func0<Observable<List<ForecastItem>>> {
  private final Lazy<ForecastRepository> forecastRepositoryLazy;

  @Inject ForecastLoader(Lazy<ForecastRepository> forecastRepositoryLazy) {
    this.forecastRepositoryLazy = forecastRepositoryLazy;
  }

  @Override public Observable<List<ForecastItem>> call() {
    return forecastRepositoryLazy.get()
        .loadQueryResponseAsync()
        // Whenever database has changed, reload.
        .repeatWhen(x -> forecastRepositoryLazy.get().onChange())
        // Extract only forecast data.
        .map(x -> {
          // Kotlin will save us from these.
          if (x != null) {
            final QueryValue query = x.query();
            if (query != null) {
              final QueryResults results = query.results();
              if (results != null) {
                final Channel channel = results.channel();
                if (channel != null) {
                  final ChannelItem item = channel.item();
                  if (item != null) {
                    return item.forecast();
                  }
                }
              }
            }
          }
          return null;
        })
        .filter(x -> x != null);
  }
}