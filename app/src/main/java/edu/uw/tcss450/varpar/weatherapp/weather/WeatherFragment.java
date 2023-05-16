package edu.uw.tcss450.varpar.weatherapp.weather;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//import edu.uw.tcss450.varpar.weatherapp.AndroidManifest;
import edu.uw.tcss450.varpar.weatherapp.R;

public class WeatherFragment extends Fragment {

    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTV, tempTV, conditionTV;
    private RecyclerView weatherRV, hourlyRV;
    private TextInputEditText cityEDT;
    private ImageView backIV, iconIV, searchIV;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private ArrayList<WeatherRVModel> weatherRVHourlyArrayList;
    private WeatherRVHourly weatherRVHourlyAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String zipCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        //getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//
//        //getActivity().setContentView(R.layout.activity_main);
//        homeRL = getView().findViewById(R.id.idRLHome);
//        loadingPB = getView().findViewById(R.id.idPBLoading);
//        cityNameTV = getView().findViewById(R.id.idTVCityName);
//        tempTV = getView().findViewById(R.id.idTVTemp);
//        conditionTV = getView().findViewById(R.id.idTVCondition);
//        weatherRV = getView().findViewById(R.id.idRVWeather);
//        hourlyRV = getView().findViewById(R.id.idRVHourlyWeather);
//        cityEDT = getView().findViewById(R.id.idEDTCity);
//        backIV = getView().findViewById(R.id.idIVBack);
//        iconIV = getView().findViewById(R.id.idIVIcon);
//        searchIV = getView().findViewById(R.id.idIVSearch);
//        weatherRVModelArrayList = new ArrayList<>();
//        weatherRVAdapter = new WeatherRVAdapter(getActivity(), weatherRVModelArrayList);
//
//        weatherRVHourlyArrayList = new ArrayList<>();
//        weatherRVHourlyAdapter = new WeatherRVHourly(getActivity(), weatherRVHourlyArrayList);
//
//        weatherRV.setAdapter(weatherRVAdapter); //this is the RV for daily weather
//        hourlyRV.setAdapter(weatherRVHourlyAdapter); //RV for hourly
//
//        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
////        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////            requestPermissions( new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
////        }
////
////        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
////        zipCode = getCityName(location.getLongitude(), location.getLatitude());
////        getWeatherInfo(zipCode);
//        searchIV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String city = cityEDT.getText().toString();
//                if(city.isEmpty()) {
//                    Toast.makeText(getActivity(), "AYOOO BRO TYPE SOMETHING FIRST GAWD", Toast.LENGTH_SHORT).show();
//                } else {
//                    cityNameTV.setText(zipCode);
//                    getWeatherInfo(city);
//                }
//            }
//        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeRL = getView().findViewById(R.id.idRLHome);
        loadingPB = getView().findViewById(R.id.idPBLoading);
        cityNameTV = getView().findViewById(R.id.idTVCityName);
        tempTV = getView().findViewById(R.id.idTVTemp);
        conditionTV = getView().findViewById(R.id.idTVCondition);
        weatherRV = getView().findViewById(R.id.idRVWeather);
        hourlyRV = getView().findViewById(R.id.idRVHourlyWeather);
        cityEDT = getView().findViewById(R.id.idEDTCity);
        backIV = getView().findViewById(R.id.idIVBack);
        iconIV = getView().findViewById(R.id.idIVIcon);
        searchIV = getView().findViewById(R.id.idIVSearch);
        weatherRVModelArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(getActivity(), weatherRVModelArrayList);

        weatherRVHourlyArrayList = new ArrayList<>();
        weatherRVHourlyAdapter = new WeatherRVHourly(getActivity(), weatherRVHourlyArrayList);

        weatherRV.setAdapter(weatherRVAdapter); //this is the RV for daily weather
        hourlyRV.setAdapter(weatherRVHourlyAdapter); //RV for hourly

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
//        }
//
//        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        zipCode = getCityName(location.getLongitude(), location.getLatitude());
//        getWeatherInfo(zipCode); //this is all to get current location so the app displays it when opned
//
        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityEDT.getText().toString();
                if(city.isEmpty()) {
                    Toast.makeText(getActivity(), "AYOOO BRO TYPE SOMETHING FIRST GAWD", Toast.LENGTH_SHORT).show();
                } else {
                    cityNameTV.setText(zipCode);
                    getWeatherInfo(city);
                }
            }
        });
        weatherRVModelArrayList.clear();
        weatherRVHourlyArrayList.clear();
        getTacomaWeatherInfo();

//        for(int i = 0;i < 5; i++) {
//            String time = "time";
//            String temper = "temp_c";
//            String img = "@mipmap/ic_launcher";
//            String wind = "wind_kph";
//            weatherRVModelArrayList.add(new WeatherRVModel(time, temper, img, wind));
//        }
//        weatherRVAdapter.notifyDataSetChanged();
//
//        for(int i = 0;i < 24; i++) {
//            String time = "time";
//            String temper = "temp_c";
//            String img = "@mipmap/ic_launcher";
//            String wind = "wind_kph";
//            weatherRVHourlyArrayList.add(new WeatherRVModel(time, temper, img, wind));
//        }
//        weatherRVHourlyAdapter.notifyDataSetChanged();

    }

//        @Override
//    public void registerForActivityResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.registerForActivityResult(requestCode, permissions, grantResults);
//        if(requestCode==PERMISSION_CODE){
//
//        }
//    }

    private String getCityName(double longitude, double latitide) {
        String cityName = "Tacoma";
        Geocoder gcd = new Geocoder(getActivity().getBaseContext(), Locale.getDefault());
        try {
            List<Address> addressList = gcd.getFromLocation(latitide,longitude,10);
            for(Address adr : addressList) {
                if(adr!=null) {
                    String city = adr.getLocality();
                    if(city!=null && !city.equals("")) {
                        cityName = city;
                    } else {
                        Log.d("Tag", "City not found");
                        Toast.makeText(getActivity(),"User city not found...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }
    private void getTacomaWeatherInfo() {
        String URL = "https://theweatherapp.herokuapp.com/weather?zipcode=Tacoma";
//        String URL = R.string.url + "weather?zipcode=Tacoma";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);

                try {
                    String cityName = response.getJSONObject("location").getString("name");
                    cityNameTV.setText("Tacoma");
                    String temp = response.getJSONObject("current").getString("temp_f");
                    tempTV.setText(temp+"°F");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(iconIV);
                    conditionTV.setText(condition);

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecast0 = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecast0.getJSONArray("hour");

                    for(int i = 0;i < 23; i++) {
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_f");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String cond = hourObj.getJSONObject("condition").getString("text");
                        weatherRVHourlyArrayList.add(new WeatherRVModel(time, temper, img, cond));
                    }
                    weatherRVHourlyAdapter.notifyDataSetChanged();

                    int day = 1;
                    JSONArray forecastArray = forecastObj.getJSONArray("forecastday");

                    for(int i = 0;i < 5; i++) {
                        JSONObject forecastDayObj = forecastArray.getJSONObject(i);
                        JSONObject dayObj = forecastDayObj.getJSONObject("day");
                        String date = forecastDayObj.getString("date");
                        String temper = dayObj.getString("maxtemp_f");
                        String img = dayObj.getJSONObject("condition").getString("icon");
                        String cond = dayObj.getJSONObject("condition").getString("text");
                        weatherRVModelArrayList.add(new WeatherRVModel(date, temper, img, cond));
                        //day++;
                    }
                    weatherRVAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Please enter valid city name...", Toast.LENGTH_SHORT).show();

            }
        });

        requestQueue.add(jsonObjectRequest);
    }
    private void getWeatherInfo(String zipCode) {
        String URL = "https://theweatherapp.herokuapp.com/weather?zipcode=" + zipCode;
//        String URL = R.string.url + "weather?zipcode=" + zipCode;
        //cityNameTV.setText(zipCode);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                weatherRVModelArrayList.clear();
                weatherRVHourlyArrayList.clear(); //do the same for hourly data

                try {
                    String cityName = response.getJSONObject("location").getString("name");
                    cityNameTV.setText(cityName);
                    String temp = response.getJSONObject("current").getString("temp_f");
                    tempTV.setText(temp+"°F");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(iconIV);
                    conditionTV.setText(condition);
                    if(isDay==1){
                        //Picasso.get().load();
                    } else {
                        //Picasso.get().load();
                    }

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecast0 = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecast0.getJSONArray("hour");

                    for(int i = 0;i < 24; i++) {
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_f");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String cond = hourObj.getJSONObject("condition").getString("text");
                        weatherRVHourlyArrayList.add(new WeatherRVModel(time, temper, img, cond));
                    }
                    weatherRVHourlyAdapter.notifyDataSetChanged();

                    int day = 1;
                    JSONArray forecastArray = forecastObj.getJSONArray("forecastday");

                    for(int i = 0;i < 5; i++) {
                        JSONObject forecastDayObj = forecastArray.getJSONObject(i);
                        JSONObject dayObj = forecastDayObj.getJSONObject("day");
                        String date = forecastDayObj.getString("date");
                        String temper = dayObj.getString("maxtemp_f");
                        String img = dayObj.getJSONObject("condition").getString("icon");
                        String cond = dayObj.getJSONObject("condition").getString("text");
                        weatherRVModelArrayList.add(new WeatherRVModel(date, temper, img, cond));
                        //day++;
                    }
                    weatherRVAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Please enter valid city name...", Toast.LENGTH_SHORT).show();

            }
        });

        requestQueue.add(jsonObjectRequest);
    }


}
