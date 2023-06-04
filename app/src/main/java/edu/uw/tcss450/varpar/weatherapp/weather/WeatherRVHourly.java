package edu.uw.tcss450.varpar.weatherapp.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.databinding.WeatherRvHourlyBinding;

/**
 * Recycler view for hourly weather information.
 */
public class WeatherRVHourly extends RecyclerView.Adapter<WeatherRVHourly.ViewHolder> {

    /** Context. */
    private final Context context;

    /** List of weather conditions. */
    private final ArrayList<WeatherRVModel> weatherRVModelArrayList;

    /**
     * Constructor that takes a list of weather conditions to display.
     * @param cont Context.
     * @param weatherR List of weather.
     */
    public WeatherRVHourly(final Context cont, final ArrayList<WeatherRVModel> weatherR) {
        this.context = cont;
        this.weatherRVModelArrayList = weatherR;
    }

    @NonNull
    @Override
    public WeatherRVHourly.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_hourly, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRVHourly.ViewHolder holder, int position) {
        WeatherRVModel model = weatherRVModelArrayList.get(position);

        holder.mBinding.idTVTemp.setText(model.getTemp() + "°F");
        Picasso.get().load("http:".concat(model.getIcon())).into(holder.mBinding.idTVCondition);
        holder.mBinding.idTVWindSpeed.setText(model.getCondition());
        SimpleDateFormat input = new SimpleDateFormat("yyyy-mm-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try {
            Date t = input.parse(model.getTime());
            holder.mBinding.idTVTime.setText(output.format(t));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherRVModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        /** Binding for view elements. */
        private final WeatherRvHourlyBinding mBinding;

        /**
         * Construct card.
         * @param itemView view holding card.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = WeatherRvHourlyBinding.bind(itemView);
        }
    }
}
