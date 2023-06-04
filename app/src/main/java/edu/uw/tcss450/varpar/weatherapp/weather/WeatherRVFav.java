package edu.uw.tcss450.varpar.weatherapp.weather;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import edu.uw.tcss450.varpar.weatherapp.MainActivity;
import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.io.RequestQueueSingleton;
import edu.uw.tcss450.varpar.weatherapp.model.UserInfoViewModel;

public class WeatherRVFav extends RecyclerView.Adapter<WeatherRVFav.ViewHolder>{

    /** . */
    private Context context;

    /** . */
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;

    /** . */
    private WeatherFavCityDialog weatherFavCityDialog;

    /** . */
    private WeatherFragment wf;

    /**
     * constructor method for Dialog Frag class.
     * @param context
     * @param weatherRVModelArrayList
     * @param weatherFavCityDialog
     */
    public WeatherRVFav(Context context, ArrayList<WeatherRVModel> weatherRVModelArrayList, WeatherFavCityDialog weatherFavCityDialog) {
        this.context = context;
        this.weatherRVModelArrayList = weatherRVModelArrayList;
        this.weatherFavCityDialog = weatherFavCityDialog;
    }

    /**
     * constructor for WeatherFragment class.
     * @param context
     * @param weatherRVModelArrayList
     * @param weatherFragment
     */
    public WeatherRVFav(Context context, ArrayList<WeatherRVModel> weatherRVModelArrayList, WeatherFragment weatherFragment) {
        this.context = context;
        this.weatherRVModelArrayList = weatherRVModelArrayList;
        this.wf = weatherFragment;
    }

    @NonNull
    @Override
    public WeatherRVFav.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_rv_weather,parent,false);
        return new WeatherRVFav.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRVFav.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WeatherRVModel model = weatherRVModelArrayList.get(position);
        holder.favTV.setText(model.getCityName());
        holder.favTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = holder.favTV.getText().toString();
                weatherFavCityDialog.helperMethod(city);
            }
        });
        holder.deleteIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weatherFavCityDialog.deleteFavLocation(position);
                weatherRVModelArrayList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, weatherRVModelArrayList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return weatherRVModelArrayList.size();
    }

    /**
     * ViewHolder inner class
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        /** fav city text view. */
        public TextView favTV;

        /** button for delete. */
        public ImageButton deleteIB;

        /**
         * ViewHolder constructor
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            favTV = itemView.findViewById(R.id.idTVFavCityName);
            deleteIB = itemView.findViewById(R.id.idIBdelete);
        }
    }

}
