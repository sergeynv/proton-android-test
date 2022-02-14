package ch.protonmail.android.protonmailtest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
                .inflate(android.R.layout.simple_list_item_2, parent, false);
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
        // Trigger UI update.
        notifyDataSetChanged();
    }

    private void onItemClick(int position) {
        final DayForecast df = mForecasts.get(position);
        mOnItemClickedCallback.accept(df);
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView subtitle;

        ViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(android.R.id.text1);
            subtitle = view.findViewById(android.R.id.text2);
            view.setOnClickListener(v -> onItemClick(getBindingAdapterPosition()));
        }
    }
}
