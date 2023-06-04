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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.contact.Contact;
import edu.uw.tcss450.varpar.weatherapp.databinding.DialogRvWeatherBinding;
import edu.uw.tcss450.varpar.weatherapp.databinding.DialogWeatherFavBinding;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentHomeBinding;
import edu.uw.tcss450.varpar.weatherapp.io.RequestQueueSingleton;
import edu.uw.tcss450.varpar.weatherapp.model.UserInfoViewModel;
/** Dialog class extends DialogFragment used to display fav locations. */
public class WeatherFavCityDialog extends DialogFragment {

    /** WeatherRVFav class to get data. */
    private WeatherRVFav weatherRVFav;

    /** Arraylist to store RV. */
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;

    /** Recycler view for cities. */
    private RecyclerView favCityRV;

    /** global name of current fav city. */
    public String favCityName;

    /** UserInfoViewModel to get user info. */
    private UserInfoViewModel mUserModel;

    /** mJwt for database stuff. */
    private String mJwt;

    /** binding stuff for dialog RV. */
    private DialogRvWeatherBinding rvWeatherBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported.
                favCityName = bundle.getString("bundleKey");
                // Do something with the result.
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rvWeatherBinding= DialogRvWeatherBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.dialog_weather_fav, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUserModel = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);
        mJwt = mUserModel.getJwt();

        favCityRV = getView().findViewById(R.id.idRVFavCities);

        weatherRVModelArrayList = new ArrayList<>();
        weatherRVFav = new WeatherRVFav(getActivity(), weatherRVModelArrayList, this);
        favCityRV.setAdapter(weatherRVFav);

        Bundle bundle = getArguments(); //getting data from weather frag
        favCityName = bundle.getString("bundleKey");
        getFavLocations();
        weatherRVFav.notifyDataSetChanged();
    }

    /**
     * retrieves users fav locations from database.
     */
    public void getFavLocations() {
        String URL = "https://theweatherapp.herokuapp.com/location/getall?user="
                    + mUserModel.getMemberID();
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null, //no body for this get request
                this::handleSuccess,
                this::handleError) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", mJwt);
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
     * Retrieve JSONObject of contacts and parse into list, notify observers.
     * @param response JSONObject of contacts.
     */
    private void handleSuccess(final JSONObject response) {

        try {
            JSONArray locations = response.getJSONArray("locations");
            for (int i = 0; i <= locations.length(); i++) {
                JSONObject nickname = locations.getJSONObject(i);
                if (!weatherRVModelArrayList.contains(nickname)) {
                    // don't add a duplicate
                    weatherRVModelArrayList.add(new WeatherRVModel(nickname.getString("nickname")));
                    weatherRVFav.notifyDataSetChanged();
                } else {
                    // this shouldn't happen but could with the async nature of the application
                    Log.wtf("ERROR", "Contact already received: ");
                }

            }
        } catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ContactListViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
    }

    /**
     * Error occurred in server communication to gain contacts.
     * @param error error that happened.
     */
    private void handleError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            Log.e("NETWORK ERROR", error.getMessage());
        } else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.e("CLIENT ERROR",
                    error.networkResponse.statusCode + " " + data);
        }
    }

    /**
     * deletes the deleted fav location from the database.
     * @param position
     */
    public void deleteFavLocation(int position) {
        String URL = "https://theweatherapp.herokuapp.com/location/removefavorite";
        JSONObject body = new JSONObject();
        try {
            body.put("user", mUserModel.getMemberID());
            body.put("nickname", weatherRVModelArrayList.get(position).getCityName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                body,
                this::deleteHandleSuccess,
                this::deleteHandleError) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", mJwt);
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
     * Success handler for deleting contact.
     * @param response server response.
     */
    private void deleteHandleSuccess(final JSONObject response) {
        try { //note what type of connection happened
            response.put("type", "delete");
            response.put("success", "true");
        } catch (JSONException e) {
            Log.wtf("JSON Error", e.getMessage());
        }
    }

    /**
     * Error handler for deleting contact.
     * @param error server response.
     */
    private void deleteHandleError(final VolleyError error) {
        JSONObject resp = new JSONObject();
        try { //note what type of connection happened
            resp.put("type", "delete");
            resp.put("success", "false");
        } catch (JSONException e) {
            Log.wtf("JSON Error", e.getMessage());
        }

        if (Objects.isNull(error.networkResponse)) { //server error?
            Log.wtf("NETWORK ERROR", error.getMessage());
            try {
                resp.put("message", "Network Error");
            } catch (JSONException e) {
                Log.wtf("JSON Error", e.getMessage());
            }
        } else { //client error?
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.wtf("CLIENT ERROR",
                    error.networkResponse.statusCode + " " + data);
            try {
                JSONObject dat = new JSONObject(new String(error.networkResponse.data, Charset.defaultCharset()));
                resp.put("message", dat.getString("message"));
            } catch (JSONException e) {
                Log.wtf("JSON Error", e.getMessage());
            }
        }
    }

    /**
     * helper method called in WeatherRVFav when clicking on.
     * fav location it changes the weather info to city clicked.
     * @param result
     */
    public void helperMethod(String result) {
        WeatherFragment.getInstance().getWeatherInfo(result);
    }
}
