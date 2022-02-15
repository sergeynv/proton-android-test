package ch.protonmail.android.protonmailtest;

import static ch.protonmail.android.protonmailtest.PicassoUtilsKt.cancelLoadImage;
import static ch.protonmail.android.protonmailtest.PicassoUtilsKt.loadImageFromCacheInto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * {@link RecyclerView.Adapter} used by both
 * {@link BaseDailyForecastFragment DailyForecastFragments} - {@link UpcomingFragment} and
 * {@link HottestFragment} - to display a list of {@link DayForecast forecasts}.
 * <p>
 * Note: the implementation of the class was intentionally left in Java, assuming Java is one of
 * languages skills in which are to be assessed.
 */
class DailyForecastAdapter extends RecyclerView.Adapter<DailyForecastAdapter.ViewHolder>
        implements Observer<List<DayForecast>> {
    private static final boolean DISPLAY_SUNRISE_SUNSET = false;

    private final @NonNull List<DayForecast> mForecasts = new ArrayList<>();
    private final Consumer<DayForecast> mOnItemClickedCallback;

    DailyForecastAdapter(Consumer<DayForecast> onItemClickedCallback) {
        mOnItemClickedCallback = onItemClickedCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        final View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_day_forecast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final DayForecast df = mForecasts.get(position);

        final String title = "Day: " + df.getDayIndex() + ": " + df.getDescription();
        holder.title.setText(title);

        final StringBuilder subtitleBuilder = new StringBuilder()
                .append("High: ").append(df.getHigh()).append(" | ")
                .append("Low: ").append(df.getLow()).append(" | ")
                .append("Rain: ").append(df.getRainChanceInPercent()).append('%');
        if (DISPLAY_SUNRISE_SUNSET) {
            subtitleBuilder.append(" | ")
                    .append("Sunrise: ").append(df.getSunrise()).append(" | ")
                    .append("Sunset: ").append(df.getSunset());
        }
        holder.subtitle.setText(subtitleBuilder.toString());

        // Cancel existing load request (if there is one)
        cancelLoadImage(holder.icon);
        // Clear out the existing thumbnail (if there is one).
        holder.icon.setImageDrawable(null);
        // Start new load request.
        loadImageFromCacheInto(df, holder.icon);
    }

    @Override
    public int getItemCount() {
        return mForecasts.size();
    }

    @Override
    public void onChanged(List<DayForecast> newForecast) {
        // Update the data.
        mForecasts.clear();
        if (newForecast != null) {
            mForecasts.addAll(newForecast);
        }

        // Trigger UI update
        // Note: this is triggering a "full" re-render, which generally we would need to avoid, but
        // here we do not have more fain-grained data, so this will have to do for this test
        // assignment).
        notifyDataSetChanged();
    }

    private void onItemClick(int position) {
        final DayForecast df = mForecasts.get(position);
        mOnItemClickedCallback.accept(df);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView subtitle;
        final ImageView icon;

        ViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(android.R.id.text1);
            subtitle = view.findViewById(android.R.id.text2);
            icon = view.findViewById(R.id.icon);

            // Note: that having to call onItemClick() is the reason this class is not static.
            view.setOnClickListener(v -> onItemClick(getBindingAdapterPosition()));
        }
    }
}
