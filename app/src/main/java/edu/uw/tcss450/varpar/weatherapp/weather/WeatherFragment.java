package edu.uw.tcss450.varpar.weatherapp.weather;

import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.ArrayList;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.chat.ChatGenerator;
import edu.uw.tcss450.varpar.weatherapp.chat.ChatRecyclerViewAdapter;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentWeatherBinding;

public class WeatherFragment extends Fragment {

    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTV, tempTV, conditionTV;
    private RecyclerView weatherRV;
    private TextInputEditText cityEDT;
    private ImageView backIV, iconIV, searchIV;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        //setContentView(R.layout.activity_main);
//        homeRL = findViewByID(R.id.idRLHome);
//        loadingPB = findViewByID(R.id.idPBLoading);
//        cityNameTV = findViewByID(R.id.idTVCityName);
//        tempTV = findViewByID(R.id.idTVTemp);
//        conditionTV = findViewByID(R.id.idTVCondition);
//        weatherRV = findViewByID(R.id.idRVWeather);
//        cityEDT = findViewByID(R.id.idEDTCity);
//        backIV = findViewByID(R.id.idIVBack);
//        iconIV = findViewByID(R.id.idIVIcon);
//        searchIV = findViewByID(R.id.idIVSearch);
//        weatherRVModelArrayList = new ArrayList<>();
//        weatherRVAdapter = new WeatherRVAdapter(getActivity(), weatherRVModelArrayList);
//        weatherRV.setAdapter(weatherRVAdapter);

//        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(WeatherFragment.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
//        }
//
//        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        cityName = getCityName(location.getLongitude(), location.getLatitude());
//        getWeatherInfo(cityName); //TODO: access_location should be in an activity

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
        cityEDT = getView().findViewById(R.id.idEDTCity);
        backIV = getView().findViewById(R.id.idIVBack);
        iconIV = getView().findViewById(R.id.idIVIcon);
        searchIV = getView().findViewById(R.id.idIVSearch);
        weatherRVModelArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(getActivity(), weatherRVModelArrayList);
        weatherRV.setAdapter(weatherRVAdapter);

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityEDT.getText().toString();
                if(city.isEmpty()) {
                    Toast.makeText(getActivity(), "AYOOO BRO TYPE SOMETHING FIRST GAWD", Toast.LENGTH_SHORT).show();
                } else {
                    cityNameTV.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });

        for(int i = 0;i < 5; i++) {
            String time = "time";
            String temper = "temp_c";
            String img = "@mipmap/ic_launcher";
            String wind = "wind_kph";
            weatherRVModelArrayList.add(new WeatherRVModel(time, temper, img, wind));
        }
        weatherRVAdapter.notifyDataSetChanged();

    }

    //    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode==PERMISSION_CODE){
//
//        }
//    }

    private String getCityName(double longitude, double latitide) {
        String cityName = "Tacoma";
//        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
//        try {
//            List<Address> addressList = gcd.getFromLocation(latitide,longitude,10);
//            for(Address adr : addressList) {
//                if(adr!=null) {
//                    String city = adr.getLocality();
//                    if(city!=null && !city.equals("")) {
//                        cityName = city;
//                    } else {
//                        Log.d("Tag", "City not found");
//                        Toast.makeText(getActivity(),"User city not found...", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return cityName;
    }

    private void getWeatherInfo(String cityName) {
        String URL = "https://api.weatherbit.io/v2.0/current?cities=" + cityName  + "&key=3f9754187e0b4f37b78c04f495a6cdfb";
        cityNameTV.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                weatherRVModelArrayList.clear();

                try {
                    String temp = response.getJSONObject("data").getString("temp");
                    tempTV.setText(temp+"°C");
                    int isDay = response.getJSONObject("data").getInt("is_day");
                    String condition = response.getJSONObject("data").getJSONObject("weather").getString("text");
                    String conditionIcon = response.getJSONObject("data").getJSONObject("weather").getString("icon");
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

                    for(int i = 0;i < 5; i++) {
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        weatherRVModelArrayList.add(new WeatherRVModel(time, temper, img, wind));
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
