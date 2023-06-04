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
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

//import edu.uw.tcss450.varpar.weatherapp.AndroidManifest;
import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.contact.Contact;
import edu.uw.tcss450.varpar.weatherapp.databinding.DialogRvWeatherBinding;
import edu.uw.tcss450.varpar.weatherapp.home.HomeFragment;
import edu.uw.tcss450.varpar.weatherapp.io.RequestQueueSingleton;
import edu.uw.tcss450.varpar.weatherapp.model.UserInfoViewModel;

public class WeatherFragment extends Fragment {
    /** Relative layout for fragment. */
    private RelativeLayout homeRL;
    /** When loading data. */
    private ProgressBar loadingPB;
    /** Text views for city name, temp, condition and todays weather. */
    private TextView cityNameTV, tempTV, conditionTV, todayWeatherTV;
    /** Recycler views for hourly weather and 5 day forecast. */
    private RecyclerView weatherRV, hourlyRV;
    /** Text box to search for city. */
    private TextInputEditText cityEDT;
    /** Image views for back, weather icon, search and fav location. */
    private ImageView backIV, iconIV, searchIV, starIV;
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
    private WeatherFavCityDialog dialog = new WeatherFavCityDialog();
    /**
     * gets the instance of WeatherFragment.
     * @return instance
     */
    public static WeatherFragment getInstance() {
        return instance;
    }

    /**
     * onCreate method.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            /**
             * to get bundle result.
             * @param bundle
             * @param requestKey
             */
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                zipCode = bundle.getString("bundleKey");
            }
        });
    }
    /**
     * onCreateView method.
     * @param savedInstanceState
     * @param container
     * @param inflater
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }
    /**
     * onViewCreated method.
     * @param savedInstanceState
     * @param view
     */
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
        mUserModel = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);
        weatherRVModelArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(getActivity(), weatherRVModelArrayList);

        weatherRVHourlyArrayList = new ArrayList<>();
        weatherRVHourlyAdapter = new WeatherRVHourly(getActivity(), weatherRVHourlyArrayList);

        weatherRVFavCityArrayList = new ArrayList<>();
        weatherRVFavAdapter = new WeatherRVFav(getActivity(), weatherRVHourlyArrayList,  this);

        weatherRV.setAdapter(weatherRVAdapter); //this is the RV for daily weather
        hourlyRV.setAdapter(weatherRVHourlyAdapter); //RV for hourly

        requestLocationUpdates();

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
            /** onClick when searching for city.
             * @param view
             */
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
            /** onClick when searching for city.
             * @param view
             */
            @Override
            public void onClick(View view) {
                addFavLocation();
                String city = cityNameTV.getText().toString();
                weatherRVFavCityArrayList.add(new WeatherRVModel(city));
                weatherRVFavAdapter.notifyDataSetChanged();

                Bundle result = new Bundle(); //sending data to dialog
                result.putString("bundleKey", city);
                getParentFragmentManager().setFragmentResult("requestKey", result);
                dialog.setArguments(result);
                dialog.show(getChildFragmentManager(), "Favorite Cities");
            }
        });
    }
    /** pings location manager to get current loaction.
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
                //ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
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
    /** shakes awake current location. */
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
    /** depricated onRequestPermissionsResult method. */
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
    /** returns the name of city of users current location.
     * @param latitide
     * @param longitude
     * @return String name of city
     */
    private String getCityName(double longitude, double latitide) {
        String cityName = "Not Found";
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
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }
    /** pings weather API to get weather info.
     * @param zipCode
     */
    public void getWeatherInfo(String zipCode) {
        String URL = getText(R.string.url) + "weather?zipcode=" + zipCode;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            /** onResponse method. */
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
                    tempTV.setText(temp+"°F");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(iconIV);
                    conditionTV.setText(condition);

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

                    JSONArray forecastArray = forecastObj.getJSONArray("forecastday");
                    for(int i = 0;i < 5; i++) {
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
    /** adds location to users favs in database. */
    public void addFavLocation() {
        String URL = "https://theweatherapp.herokuapp.com/location/addfavorite";
        JSONObject body = new JSONObject();
        String Jwt = mUserModel.getJwt();

        try {
            body.put("user", mUserModel.getMemberID());
            body.put("nickname", cityNameTV.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                body,
                this::postHandleSuccess,
                this::postHandleError) {
            /** magic method for auth.
             * @return Map
             */
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
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
    /** handle success when location added.
     * @param response
     */
    private void postHandleSuccess(final JSONObject response) {
        try { //note what type of connection happened
            response.put("type", "post");
        } catch (JSONException e) {
            Log.e("JSON Error", e.getMessage());
        }
        showDialog();
    }
    /** handle error for adding location.
     * @param error
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
        String city = cityNameTV.getText().toString();
        Bundle result = new Bundle(); //sending data to dialog
        result.putString("bundleKey", city);
        getParentFragmentManager().setFragmentResult("requestKey", result);

        WeatherFavCityDialog d = new WeatherFavCityDialog();
        d.setArguments(result);
        d.show(getChildFragmentManager(), "Favorite Cities");
    }
}
