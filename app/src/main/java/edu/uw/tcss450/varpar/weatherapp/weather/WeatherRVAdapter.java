package edu.uw.tcss450.varpar.weatherapp.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.databinding.WeatherRvItemBinding;

/**
 * Recycler view adapter for daily weather items.
 */
public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder> {

    /** Context. */
    private final Context context;

    /** List of weather conditions. */
    private final ArrayList<WeatherRVModel> weatherRVModelArrayList;

    /**
     * Constructor that takes list of weather conditions.
     * @param cont context.
     * @param weatherR List of weather conditions.
     */
    public WeatherRVAdapter(final Context cont, final ArrayList<WeatherRVModel> weatherR) {
        this.context = cont;
        this.weatherRVModelArrayList = weatherR;
    }

    @NonNull
    @Override
    public WeatherRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRVAdapter.ViewHolder holder, int position) {
        WeatherRVModel model = weatherRVModelArrayList.get(position);
        holder.mBinding.idTVTemp.setText(model.getTemp() + "°F");
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.mBinding.idTVCondition);
        holder.mBinding.idTVWindSpeed.setText(model.getCondition());
        holder.mBinding.idTVTime.setText(model.getTime());
    }

    @Override
    public int getItemCount() {
        return weatherRVModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        /** Binding for view elements. */
        private final WeatherRvItemBinding mBinding;

        /**
         * Constructor to bind view elements.
         * @param itemView view of card.
         */
        public ViewHolder(final @NonNull View itemView) {
            super(itemView);
            mBinding = WeatherRvItemBinding.bind(itemView);
        }
    }
}
