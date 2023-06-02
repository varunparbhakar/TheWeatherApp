package edu.uw.tcss450.varpar.weatherapp.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.uw.tcss450.varpar.weatherapp.R;

public class WeatherRVFav extends RecyclerView.Adapter<WeatherRVFav.ViewHolder>{

    private Context context;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;

    public WeatherRVFav(Context context, ArrayList<WeatherRVModel> weatherRVModelArrayList) {
        this.context = context;
        this.weatherRVModelArrayList = weatherRVModelArrayList;
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
                //WeatherFragment wf = (WeatherFragment) getActivity().getSupportFragmentManager().findFragmentByTag("WeatherFragment");
//                WeatherFragment wf = new WeatherFragment();
//                wf.getWeatherInfo(city);
                WeatherFragment.getInstance().getWeatherInfo(city);
//                Bundle result = new Bundle(); //sending data to dialog
//                result.putString("bundleKey", city);
//                //getParentFragmentManager().setFragmentResult("requestKey", result);
//
//                WeatherFragment frag = new WeatherFragment();
//                frag.setArguments(result);
            }
        });
        holder.deleteIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
//    public static void updateAdapter() {
//        notifyDataSetChanged();
//    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView favTV;
        public ImageButton deleteIB;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            favTV = itemView.findViewById(R.id.idTVFavCityName);
            deleteIB = itemView.findViewById(R.id.idIBdelete);
        }
    }

}
