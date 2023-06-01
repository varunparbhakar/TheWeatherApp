package edu.uw.tcss450.varpar.weatherapp.weather;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.uw.tcss450.varpar.weatherapp.R;

public class WeatherFavCityDialog extends DialogFragment {

    public TextView favCityTV;
    private ImageButton deleteIB;
    private WeatherRVFav weatherRVFav;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;
    private RecyclerView favCityRV;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_weather_fav, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        favCityTV = (TextView) view.findViewById(R.id.idTVFavCityName);
//        deleteIB = view.findViewById(R.id.idIBdelete);
        favCityRV = getView().findViewById(R.id.idRVFavCities);

        weatherRVModelArrayList = new ArrayList<>();
        weatherRVFav = new WeatherRVFav(getActivity(), weatherRVModelArrayList);
        favCityRV.setAdapter(weatherRVFav);
        getFavCities();
        favCityTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = favCityTV.getText().toString();
//                WeatherFragment wf = (WeatherFragment) getActivity().getSupportFragmentManager().findFragmentByTag("WeatherFragment");
//                wf.zipCode = city;
                Bundle result = new Bundle();
                result.putString("bundleKey", city);
                getParentFragmentManager().setFragmentResult("requestKey", result);
            }
        });
    }

    public void getFavCities() {
        String city = "jhgdf";
        weatherRVModelArrayList.clear();
        //WeatherFragment wf = (WeatherFragment) getActivity().getSupportFragmentManager().findFragmentByTag("WeatherFragment");
        WeatherFragment wf = new WeatherFragment();

        for(int i = 0; i < 5; i++) {
            city = "jkhndf";
            weatherRVModelArrayList.add(new WeatherRVModel(city));
        }
        weatherRVFav.notifyDataSetChanged();
    }
}
