package thuytrinh.forecastbird.api;

import android.support.annotation.Nullable;

import com.google.gson.annotations.JsonAdapter;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@Gson.TypeAdapters
@JsonAdapter(GsonAdaptersChannelItem.class)
public interface ChannelItem {
  @Nullable List<ForecastItem> forecast();
}