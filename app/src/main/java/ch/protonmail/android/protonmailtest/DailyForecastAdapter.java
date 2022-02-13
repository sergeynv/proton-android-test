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

class DailyForecastAdapter extends RecyclerView.Adapter<DailyForecastAdapter.ViewHolder>
        implements Observer<List<DayForecast>> {
    private static final boolean DISPLAY_SUNRISE_SUNSET = false;

    private final @NonNull List<DayForecast> mForecast = new ArrayList<>();

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
        final DayForecast f = mForecast.get(position);

        final String title = "Day: " + f.getDayIndex() + ": " + f.getDescription();
        holder.title.setText(title);

        final StringBuilder subtitleBuilder = new StringBuilder()
                .append("High: ").append(f.getHigh()).append(" | ")
                .append("Low: ").append(f.getLow()).append(" | ")
                .append("Rain: ").append(f.getRainChanceInPercent() + '%');
        if (DISPLAY_SUNRISE_SUNSET) {
            subtitleBuilder.append(" | ")
                    .append("Sunrise: ").append(f.getSunrise()).append(" | ")
                    .append("Sunset: ").append(f.getSunset());
        }
        holder.subtitle.setText(subtitleBuilder.toString());
    }

    @Override
    public int getItemCount() {
        return mForecast.size();
    }

    @Override
    public void onChanged(List<DayForecast> newForecast) {
        // Update the data.
        mForecast.clear();
        if (newForecast != null) {
            mForecast.addAll(newForecast);
        }
        // Trigger UI update.
        notifyDataSetChanged();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView subtitle;

        ViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(android.R.id.text1);
            subtitle = view.findViewById(android.R.id.text2);
        }
    }
}
