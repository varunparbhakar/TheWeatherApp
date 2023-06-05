package edu.uw.tcss450.varpar.weatherapp.weather;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentWeatherBinding;
import edu.uw.tcss450.varpar.weatherapp.io.RequestQueueSingleton;
import edu.uw.tcss450.varpar.weatherapp.model.UserInfoViewModel;

public class WeatherFragment extends Fragment {

    /** Arraylist for WeatherRVModel used for forecast. */
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;

    /** WeatherRVAdapter class for forecaset day RV. */
    private WeatherRVAdapter weatherRVAdapter;

    /** Arraylist for WeatherRVModel used for hourly RV. */
    private ArrayList<WeatherRVModel> weatherRVHourlyArrayList;

    /** WeatherRVHourly for hourly RV. */
    private WeatherRVHourly weatherRVHourlyAdapter;

    /** Arraylist for WeatherRVModel used for storing fav cities. */
    public ArrayList<WeatherRVModel> weatherRVFavCityArrayList;

    /** WeatherRVFav for fav city RV. */
    public WeatherRVFav weatherRVFavAdapter;

    /** LocationManager for getting current location. */
    private LocationManager locationManager;

    /** pulls user data from UserInfoViewModel. */
    private UserInfoViewModel mUserModel;

    /** used to pass user input into weather API. */
    public String zipCode;

    /** WeatherFragment instance. */
    private static WeatherFragment instance;

    /** global variable to use throughout code. */
    private final WeatherFavCityDialog dialog = new WeatherFavCityDialog();

    /** Binding for fragment view objects. */
    private FragmentWeatherBinding mBinding;

    /**
     * Gets the instance of WeatherFragment.
     * @return instance
     */
    public static WeatherFragment getInstance() {
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> zipCode = bundle.getString("bundleKey"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding = FragmentWeatherBinding.bind(requireView());

        mUserModel = new ViewModelProvider(requireActivity()).get(UserInfoViewModel.class);
        weatherRVModelArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(getActivity(), weatherRVModelArrayList);

        weatherRVHourlyArrayList = new ArrayList<>();
        weatherRVHourlyAdapter = new WeatherRVHourly(getActivity(), weatherRVHourlyArrayList);

        weatherRVFavCityArrayList = new ArrayList<>();
        weatherRVFavAdapter = new WeatherRVFav(getActivity(), weatherRVHourlyArrayList,  this);

        mBinding.idRVWeather.setAdapter(weatherRVAdapter); //this is the RV for daily weather
        mBinding.idRVHourlyWeather.setAdapter(weatherRVHourlyAdapter); //RV for hourly

        requestLocationUpdates();

        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
            Location location = getLastKnownLocation();
            if (location == null) {
                requestLocationUpdates();
            }
            zipCode = getCityName(location.getLongitude(), location.getLatitude());
            getWeatherInfo(zipCode); //this is all to get current location so the app displays it when opned
        } else {
            mBinding.idPBLoading.setVisibility(View.GONE);
            mBinding.idRLHome.setVisibility(View.VISIBLE);
            mBinding.idTVCityName.setText(R.string.search_for_city);
            mBinding.idTVTemp.setVisibility(View.GONE);
            mBinding.idIVIcon.setVisibility(View.GONE);
            mBinding.idTVCondition.setVisibility(View.GONE);
            mBinding.idTVTodayWeather.setVisibility(View.GONE);
        }

        mBinding.idIVSearch.setOnClickListener(view1 -> {
            String city = mBinding.idEDTCity.getText().toString();
            if (city.isEmpty()) {
                Toast.makeText(getActivity(), "AYOOO BRO TYPE SOMETHING FIRST GAWD", Toast.LENGTH_SHORT).show();
            } else {
                mBinding.idTVCityName.setText(zipCode);
                getWeatherInfo(city);
            }
        });

        mBinding.idIVstar.setOnClickListener(view12 -> {
            addFavLocation();
            String city = mBinding.idTVCityName.getText().toString();
            weatherRVFavCityArrayList.add(new WeatherRVModel(city));
            weatherRVFavAdapter.notifyDataSetChanged();

            Bundle result = new Bundle(); //sending data to dialog
            result.putString("bundleKey", city);
            getParentFragmentManager().setFragmentResult("requestKey", result);
            dialog.setArguments(result);
            dialog.show(getChildFragmentManager(), "Favorite Cities");
        });
    }

    /**
     * Pings location manager to get current location.
     * @return Location
     */
    private Location getLastKnownLocation() {
        requestLocationUpdates();
        LocationManager mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            requestLocationUpdates();
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            Log.d("l", "l is " + l);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    /**
     * Shakes awake current location
     */
    private void requestLocationUpdates() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
            }
        };
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
        LocationServices.getFusedLocationProviderClient(requireActivity()).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    /** Depreciated onRequestPermissionsResult method. */
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
        } else {
            Log.wtf("uwu", "permission request failed");
        }
    }

    /**
     * Returns the name of city of users current location.
     * @param latitide lat of location
     * @param longitude long of location
     * @return String name of city
     */
    private String getCityName(final double longitude, final double latitide) {
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
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    /**
     * Pings weather API to get weather info.
     * @param zipC zipcode to ping.
     */
    public void getWeatherInfo(final String zipC) {
        String URL = getText(R.string.url) + "weather?zipcode=" + zipC;
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            /** onResponse method. */
            @Override
            public void onResponse(JSONObject response) {
                mBinding.idPBLoading.setVisibility(View.GONE);
                mBinding.idRLHome.setVisibility(View.VISIBLE);
                mBinding.idTVTemp.setVisibility(View.VISIBLE);
                mBinding.idIVIcon.setVisibility(View.VISIBLE);
                mBinding.idTVCondition.setVisibility(View.VISIBLE);
                mBinding.idTVTodayWeather.setVisibility(View.VISIBLE);

                weatherRVModelArrayList.clear();
                weatherRVHourlyArrayList.clear(); //do the same for hourly data

                try {
                    String cityName = response.getJSONObject("location").getString("name");
                    mBinding.idTVCityName.setText(cityName);
                    String temp = response.getJSONObject("current").getString("temp_f");
                    mBinding.idTVTemp.setText(temp + "°F");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(mBinding.idIVIcon);
                    mBinding.idTVCondition.setText(condition);

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

                    JSONArray forecastArray = forecastObj.getJSONArray("forecastday");
                    for (int i = 0; i < 5; i++) {
                        JSONObject forecastDayObj = forecastArray.getJSONObject(i);
                        JSONObject dayObj = forecastDayObj.getJSONObject("day");
                        String date = forecastDayObj.getString("date");
                        String temper = dayObj.getString("maxtemp_f");
                        String img = dayObj.getJSONObject("condition").getString("icon");
                        String cond = dayObj.getJSONObject("condition").getString("text");
                        weatherRVModelArrayList.add(new WeatherRVModel(date, temper, img, cond));
                    }
                    weatherRVAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    weatherErrorToast();
                }
            }
        }, new Response.ErrorListener() {
            /** catch error. */
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

    /** Adds location to users favs in database. */
    public void addFavLocation() {
        String URL = "https://theweatherapp.herokuapp.com/location/addfavorite";
        JSONObject body = new JSONObject();
        String Jwt = mUserModel.getJwt();

        try {
            body.put("user", mUserModel.getMemberID());
            body.put("nickname", mBinding.idTVCityName.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                body,
                this::postHandleSuccess,
                this::postHandleError) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", Jwt);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(requireContext().getApplicationContext())
                .addToRequestQueue(request);
    }
    /**
     * Handle success when location added.
     * @param response response from server.
     */
    private void postHandleSuccess(final JSONObject response) {
        try { //note what type of connection happened
            response.put("type", "post");
        } catch (JSONException e) {
            Log.e("JSON Error", e.getMessage());
        }
        showDialog();
    }

    /**
     * Handle error for adding location.
     * @param error volley or server error.
     */
    private void postHandleError(final VolleyError error) {
        JSONObject resp = new JSONObject();
        try { //note what type of connection happened
            resp.put("type", "post");
        } catch (JSONException e) {
            Log.e("JSON Error", e.getMessage());
        }

        if (Objects.isNull(error.networkResponse)) { //server error?
            Log.e("NETWORK ERROR", error.getMessage());
            try {
                resp.put("message1", "Network Error");
            } catch (JSONException e) {
                Log.e("JSON Error", e.getMessage());
            }
        } else { //client error?
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.e("CLIENT ERROR",
                    error.networkResponse.statusCode + " " + data);
            try {
                JSONObject dat = new JSONObject(new String(error.networkResponse.data, Charset.defaultCharset()));
                resp.put("message2", dat.getString("message"));
            } catch (JSONException e) {
                Log.e("JSON Error", e.getMessage());
            }
        }
    }
    /** helper method to display dialog. */
    public void showDialog() {
        dialog.dismiss();
        String city = mBinding.idTVCityName.getText().toString();
        Bundle result = new Bundle(); //sending data to dialog
        result.putString("bundleKey", city);
        getParentFragmentManager().setFragmentResult("requestKey", result);

        WeatherFavCityDialog d = new WeatherFavCityDialog();
        d.setArguments(result);
        d.show(getChildFragmentManager(), "Favorite Cities");
    }
}
