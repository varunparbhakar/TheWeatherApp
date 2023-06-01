package edu.uw.tcss450.varpar.weatherapp.weather;

import static android.content.Context.LOCATION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//import edu.uw.tcss450.varpar.weatherapp.AndroidManifest;
import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.home.HomeFragment;

public class WeatherFragment extends Fragment {

    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTV, tempTV, conditionTV, todayWeatherTV;
    private RecyclerView weatherRV, hourlyRV;
    private TextInputEditText cityEDT;
    private ImageView backIV, iconIV, searchIV, starIV;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private ArrayList<WeatherRVModel> weatherRVHourlyArrayList;
    private WeatherRVHourly weatherRVHourlyAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    public String zipCode;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported.
                zipCode = bundle.getString("bundleKey");
                // Do something with the result.
            }
        });
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
        starIV = getView().findViewById(R.id.idIVstar);
        todayWeatherTV = getView().findViewById(R.id.idTVTodayWeather);
        weatherRVModelArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(getActivity(), weatherRVModelArrayList);

        weatherRVHourlyArrayList = new ArrayList<>();
        weatherRVHourlyAdapter = new WeatherRVHourly(getActivity(), weatherRVHourlyArrayList);

        weatherRV.setAdapter(weatherRVAdapter); //this is the RV for daily weather
        hourlyRV.setAdapter(weatherRVHourlyAdapter); //RV for hourly

//        weatherRVModelArrayList.clear();
//        weatherRVHourlyArrayList.clear();
        requestLocationUpdates();
//        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
//        }

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
            Location location = getLastKnownLocation();
            if (location == null) {
                requestLocationUpdates();
            }
            zipCode = getCityName(location.getLongitude(), location.getLatitude());
            getWeatherInfo(zipCode); //this is all to get current location so the app displays it when opned
        }
        else {
            loadingPB.setVisibility(View.GONE);
            homeRL.setVisibility(View.VISIBLE);
            cityNameTV.setText("Search for City");
            tempTV.setVisibility(View.GONE);
            iconIV.setVisibility(View.GONE);
            conditionTV.setVisibility(View.GONE);
            todayWeatherTV.setVisibility(View.GONE);
        }

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityEDT.getText().toString();
                if (city.isEmpty()) {
                    Toast.makeText(getActivity(), "AYOOO BRO TYPE SOMETHING FIRST GAWD", Toast.LENGTH_SHORT).show();
                } else {
                    cityNameTV.setText(zipCode);
                    getWeatherInfo(city);
                }
            }
        });
        starIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityNameTV.getText().toString();
                WeatherFavCityDialog dialog = new WeatherFavCityDialog();
                //dialog.setTargetFragment(WeatherFragment.this, 1);
                dialog.show(getChildFragmentManager(), "Favorite Cities");
                if (city.isEmpty()) {
                    Toast.makeText(getActivity(), "AYOOO BRO TYPE SOMETHING FIRST GAWD", Toast.LENGTH_SHORT).show();
                } else {
                    //wfcd.favCityTV.setText(city);
                }
            }
        });
        //getWeatherInfo("98375");
    }

    private Location getLastKnownLocation() {
        requestLocationUpdates();
        LocationManager mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            requestLocationUpdates();
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            Log.d("l", "l is " + l);
            Geocoder geo = new Geocoder(getActivity(), Locale.getDefault());
//            try {
//                List<Address> addresses = geo.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
//                Log.d("address", "address is " + addresses);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private void requestLocationUpdates() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //TODO: UI updates.
                    }
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }
        LocationServices.getFusedLocationProviderClient(getActivity()).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
            Location location = getLastKnownLocation();
            if (location == null) {
                requestLocationUpdates();
            }
            zipCode = getCityName(location.getLongitude(), location.getLatitude());
            getWeatherInfo(zipCode); //this is all to get current location so the app displays it when opned
            //getLastKnownLocation();
        } else {
            Log.wtf("uwu", "permission request failed");
        }
    }

    private String getCityName(double longitude, double latitide) {
        String cityName = "Not Found";
        Geocoder gcd = new Geocoder(getActivity().getBaseContext(), Locale.getDefault());
        try {
            List<Address> addressList = gcd.getFromLocation(latitide, longitude, 10);
            for (Address adr : addressList) {
                if (adr != null) {
                    String city = adr.getLocality();
                    if (city != null && !city.equals("")) {
                        cityName = city;
                    } else {
                        Log.d("Tag", "City not found");
                        //Toast.makeText(getActivity(),"User city not found...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    private void getWeatherInfo(String zipCode) {
        String URL = getText(R.string.url) + "weather?zipcode=" + zipCode;
        //cityNameTV.setText(zipCode);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                tempTV.setVisibility(View.VISIBLE);
                iconIV.setVisibility(View.VISIBLE);
                conditionTV.setVisibility(View.VISIBLE);
                todayWeatherTV.setVisibility(View.VISIBLE);

                weatherRVModelArrayList.clear();
                weatherRVHourlyArrayList.clear(); //do the same for hourly data

                try {
                    String cityName = response.getJSONObject("location").getString("name");
                    cityNameTV.setText(cityName);
                    String temp = response.getJSONObject("current").getString("temp_f");
                    tempTV.setText(temp + "°F");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(iconIV);
                    conditionTV.setText(condition);
                    if (isDay == 1) {
                        //Picasso.get().load();
                    } else {
                        //Picasso.get().load();
                    }

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecast0 = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecast0.getJSONArray("hour");

                    for (int i = 0; i < 24; i++) {
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

                    for (int i = 0; i < 5; i++) {
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
                    weatherErrorToast();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                weatherErrorToast();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Displays toast to user that their city name was invalid.
     */
    private void weatherErrorToast() {
        Toast.makeText(getActivity(), getText(R.string.weather_valid_city_error), Toast.LENGTH_SHORT).show();
    }
}
