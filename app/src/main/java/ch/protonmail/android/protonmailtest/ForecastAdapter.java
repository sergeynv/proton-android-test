package ch.protonmail.android.protonmailtest;

import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.DayViewHolder> {
    private final List<String> mItems = new ArrayList<>();

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // TODO: implement.
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        final String item = mItems.get(position);
        holder.titleView.setText(item);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    protected static class DayViewHolder extends RecyclerView.ViewHolder {
        final TextView titleView;

        DayViewHolder(@NonNull TextView view) {
            super(view);
            titleView = view;
        }
    }
}
