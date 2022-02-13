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

        final String title = "Day " + f.getDay() + ": " + f.getDescription();
        final String subtitle = "High: " + f.getHigh() + " | Low: " + f.getLow();

        holder.title.setText(title);
        holder.subtitle.setText(subtitle);
    }

    @Override
    public int getItemCount() {
        return mForecast.size();
    }

    @Override
    public void onChanged(List<DayForecast> newForecast) {
        // Update the data.
        mForecast.clear();
        mForecast.addAll(newForecast);
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
