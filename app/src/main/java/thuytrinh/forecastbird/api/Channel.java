package thuytrinh.forecastbird.api;

import android.support.annotation.Nullable;

import com.google.gson.annotations.JsonAdapter;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters
@JsonAdapter(GsonAdaptersChannel.class)
public interface Channel {
  @Nullable ChannelItem item();
}