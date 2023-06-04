package edu.uw.tcss450.varpar.weatherapp.home;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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
import edu.uw.tcss450.varpar.weatherapp.contact.Contact;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentHomeBinding;
import edu.uw.tcss450.varpar.weatherapp.model.UserInfoViewModel;

/**
 * Home fragment for app, contains current location weather and friend requests.
 */
public class HomeFragment extends Fragment {

    /** Permission code for location. */
    private static final int PERMISSION_CODE = 1;

    /** Binding of view elements. */
    private FragmentHomeBinding mBinding;

    /** Friend request recycler view adapter. */
    private FriendReqRVAdapter friendReqRVAdapter;

    /** User information. */
    private UserInfoViewModel mUserModel;

    /** Current city. */
    private String currentCity;

    /** Object that can dial out for location update. */
    private LocationManager locationManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        Location location = getLastKnownLocation();
        if (location == null) {
            requestLocationUpdates();
        }
        currentCity = getCityName(location.getLongitude(), location.getLatitude());

        getWeatherInfo();

        super.onViewCreated(view, savedInstanceState);
        mUserModel = new ViewModelProvider(requireActivity()).get(UserInfoViewModel.class);
        String welcomeText = getText(R.string.home_fragment_welcome) + " " + mUserModel.getFirstName();
        mBinding.textGreeting.setText(welcomeText);

        // Get the friend requests and display them
        getFriendsRequests();
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
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            Log.d("l", "l is " + l);
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

    /**
     * Shakes awake current location
     */
    private void requestLocationUpdates() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

            }
        };
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
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
            currentCity = getCityName(location.getLongitude(), location.getLatitude());
            getWeatherInfo(); //this is all to get current location so the app displays it when opned
            //getLastKnownLocation();
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
    public String getCityName(final double longitude, final double latitide) {
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

    /**
     * Pings weather API to get weather info.
     */
    private void getWeatherInfo() {
        String URL = getText(R.string.url) + "weather?zipcode=" + currentCity;
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
                    try {
                        String cityName = response.getJSONObject("location").getString("name");
                        mBinding.locationText.setText(cityName);
                        String temp = response.getJSONObject("current").getString("temp_f");
                        mBinding.temperatureText.setText(temp);
                        String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                        String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                        Picasso.get().load("http:".concat(conditionIcon)).into(mBinding.forecastImage);
                        mBinding.tempForecast.setText(condition);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> Toast.makeText(getActivity(), "Please enter valid city name...", Toast.LENGTH_SHORT).show());
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Get the friend requests from the database.
     */
    public void getFriendsRequests() {
        String URL = "https://theweatherapp.herokuapp.com/contacts/getrequests";
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());

        String Jwt = mUserModel.getJwt();

        Request request = new JsonObjectRequest(Request.Method.GET,
                URL + "?user=" + mUserModel.getMemberID(),
                null,
                this::handleResult,
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", Jwt);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    /**
     * Handle error from getting friend requests.
     * @param error volley or server error.
     */
    private void handleError(final VolleyError error) {
        Log.wtf("ERROR", error.getMessage());
        if (Objects.isNull(error.networkResponse)) {
            Log.wtf("NETWORK ERROR", error.getMessage());
        } else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.wtf("CLIENT ERROR",
                    error.networkResponse.statusCode + " " + data);
        }
    }

    /**
     * Handle successful pull of friend requests from server.
     * @param response server response.
     */
    private void handleResult(final JSONObject response) {
        Log.wtf("RESULT", response.toString());
        ArrayList<Contact> friendReqInfo = new ArrayList<>();

        try {
            if (response.has("message") && response.get("message").equals("no current requests")) {
                friendReqRVAdapter = new FriendReqRVAdapter(getActivity(), this, friendReqInfo);
                mBinding.idRVIncomingFR.setAdapter(friendReqRVAdapter);
                return;
            }
        } catch (JSONException e) {
            Log.wtf("ERROR", "unknown error");
        }

        try {
            JSONArray friendRequestsArray = response.getJSONArray("friendRequests");

            for (int i = 0; i < friendRequestsArray.length(); i++) {
                JSONObject friendRequestObject = friendRequestsArray.getJSONObject(i);
                String friendName = friendRequestObject.getString("username");
                String idName = friendRequestObject.getString("memberid");

                friendReqInfo.add(new Contact.Builder(friendName, idName).build());
            }

            friendReqRVAdapter = new FriendReqRVAdapter(getActivity(), this, friendReqInfo);
            mBinding.idRVIncomingFR.setAdapter(friendReqRVAdapter);

            friendReqRVAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
        Log.wtf("ERROR", e.getMessage());
    }
    }

    /**
     * Handle friend request accept.
     * @param response server response.
     */
    private void handleAcceptResult(final JSONObject response) {
        Toast.makeText(getActivity(), "Friend request accepted!", Toast.LENGTH_SHORT).show();
        getFriendsRequests();
    }

    /**
     * Handle friend request deny.
     * @param response server response.
     */
    private void handleRemoveResult(final JSONObject response) {
        Toast.makeText(getActivity(), "Friend removed!", Toast.LENGTH_SHORT).show();
        getFriendsRequests();
    }

    /**
     * Handle error from accepting friend requests.
     * @param error volley or server error.
     */
    private void handleAcceptError(final VolleyError error) {
        Log.wtf("ERROR", error.getMessage());
        if (Objects.isNull(error.networkResponse)) {
            Log.e("NETWORK ERROR", error.getMessage());
        } else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.e("CLIENT ERROR",
                    error.networkResponse.statusCode + " " + data);
        }
    }

    /**
     * Connect to server to accept friend request.
     * @param friendID memberID to accept request.
     */
    public void getAcceptFriendRequests(final String friendID) {
        String URL = "https://theweatherapp.herokuapp.com/contacts/acceptfriendrequest";
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());

        String Jwt = mUserModel.getJwt();

        JSONObject body = new JSONObject();
        try {
            body.put("receiver", mUserModel.getMemberID());
            body.put("sender", friendID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(Request.Method.POST,
                URL,
                body,
                this::handleAcceptResult,
                this::handleAcceptError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", Jwt);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    /**
     * Connect to server to deny friend request.
     * @param friendID memberID to deny request.
     */
    public void getRemoveFriendRequests(final String friendID) {
        String URL = "https://theweatherapp.herokuapp.com/contacts/remove/";
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());

        String Jwt = mUserModel.getJwt();

        JSONObject body = new JSONObject();
        try {
            body.put("user", mUserModel.getMemberID());
            body.put("friend", friendID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(Request.Method.POST,
                URL,
                body,
                this::handleRemoveResult,
                this::handleRemoveError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", Jwt);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    /**
     * Handle error from denying friend requests.
     * @param error volley or server error.
     */
    private void handleRemoveError(final VolleyError error) {
        Log.wtf("ERROR", error.getMessage());
        if (Objects.isNull(error.networkResponse)) {
            Log.e("NETWORK ERROR", error.getMessage());
        } else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.e("CLIENT ERROR",
                    error.networkResponse.statusCode + " " + data);
        }
    }
}
