package thuytrinh.forecastbird;

import android.content.Context;
import android.os.AsyncTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.schedulers.Schedulers;

@Module
public class AppModule {
  private final Context context;

  public AppModule(Context context) {
    this.context = context;
  }

  @Provides @Singleton Context context() {
    return context;
  }

  /**
   * Creates an optimal {@link ExecutorService} for Android apps.
   * This is cloned from latest definition of {@link AsyncTask#THREAD_POOL_EXECUTOR}.
   */
  @Provides @Singleton ExecutorService executorService() {
    final int availableProcessors = Runtime.getRuntime().availableProcessors();
    final int corePoolSize = availableProcessors + 1;
    final int maximumPoolSize = availableProcessors * 2 + 1;
    final int keepAlive = 1;
    return new ThreadPoolExecutor(
        corePoolSize,
        maximumPoolSize,
        /* This is how long a thread lives after it is no longer needed. */
        keepAlive, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>()
    );
  }

  @Provides @Singleton Scheduler scheduler(ExecutorService executorService) {
    return Schedulers.from(executorService);
  }
}