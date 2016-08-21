package thuytrinh.forecastbird.api;

import android.support.annotation.Nullable;

import com.google.gson.annotations.JsonAdapter;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters
@JsonAdapter(GsonAdaptersQueryValue.class)
public interface QueryValue {
  @Nullable QueryResults results();
}