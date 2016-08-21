package thuytrinh.forecastbird.api;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.annotations.JsonAdapter;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters
@JsonAdapter(GsonAdaptersForecastItem.class)
public abstract class ForecastItem implements Parcelable {
  public static final Creator<ForecastItem> CREATOR = new Creator<ForecastItem>() {
    @Override public ForecastItem createFromParcel(Parcel in) {
      return ImmutableForecastItem.builder()
          .code(in.readString())
          .date(in.readString())
          .day(in.readString())
          .high(in.readString())
          .low(in.readString())
          .text(in.readString())
          .build();
    }

    @Override public ForecastItem[] newArray(int size) {
      return new ForecastItem[size];
    }
  };

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(code());
    dest.writeString(date());
    dest.writeString(day());
    dest.writeString(high());
    dest.writeString(low());
    dest.writeString(text());
  }

  @Override public int describeContents() {
    return 0;
  }

  @Nullable public abstract String code();
  @Nullable public abstract String date();
  @Nullable public abstract String day();
  @Nullable public abstract String high();
  @Nullable public abstract String low();
  @Nullable public abstract String text();
}