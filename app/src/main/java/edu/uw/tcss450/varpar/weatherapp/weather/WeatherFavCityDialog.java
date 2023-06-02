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

public class WeatherFavCityDialog extends DialogFragment {

    public TextView favCityTV;
    private ImageButton deleteIB;
    private WeatherRVFav weatherRVFav;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;
    private RecyclerView favCityRV;
    public String favCityName;
    private UserInfoViewModel mUserModel;
    private DialogWeatherFavBinding mBinding;
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
        //WeatherRVFav.ViewHolder holder = new WeatherRVFav.ViewHolder(view);
        //favCityTV = holder.favTV;
        //holder.deleteIB = view.findViewById(R.id.idIBdelete);

        favCityRV = getView().findViewById(R.id.idRVFavCities);

        weatherRVModelArrayList = new ArrayList<>();
        weatherRVFav = new WeatherRVFav(getActivity(), weatherRVModelArrayList);
        favCityRV.setAdapter(weatherRVFav);

        Bundle bundle = getArguments(); //getting data from weather frag
        favCityName = bundle.getString("bundleKey");

        //getFavCities();
        getFavLocations();

        String city = favCityName; //sending data to weather frag
        Bundle result = new Bundle();
        result.putString("bundleKey", city);
        getParentFragmentManager().setFragmentResult("requestKey", result);

        WeatherFragment fragment = new WeatherFragment();
        fragment.setArguments(result);

//        favCityTV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String city = favCityTV.getText().toString();
////                WeatherFragment wf = (WeatherFragment) getActivity().getSupportFragmentManager().findFragmentByTag("WeatherFragment");
////                wf.zipCode = city;
//                Bundle result = new Bundle();
//                result.putString("bundleKey", city);
//                getParentFragmentManager().setFragmentResult("requestKey", result);

//        Bundle bundle = getArguments(); //getting data from dialog
//        zipCode = bundle.getString("bundleKey");
//        getFavCityInfo(zipCode);
//            }
//        });

    }

    public void getFavCities() {
        String city;
        //weatherRVModelArrayList.clear();
        city = favCityName;
        weatherRVModelArrayList.add(new WeatherRVModel(city));
        weatherRVFav.notifyDataSetChanged();
    }
    public void getFavLocations() {
        String URL = "https://theweatherapp.herokuapp.com/location/getall?user="
                    + mUserModel.getMemberID();
        String Jwt = mUserModel.getJwt();
        //getFavCities();

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
     * Retrieve JSONObject of contacts and parse into list, notify observers.
     * @param response JSONObject of contacts.
     */
    private void handleSuccess(final JSONObject response) {
        //weatherRVModelArrayList

        try {
            JSONArray locations = response.getJSONArray("locations");
            //String nickname = response.getString("nickname");
            for (int i = 0; i < locations.length(); i++) {
                JSONObject nickname = locations.getJSONObject(i);
                if (!weatherRVModelArrayList.contains(nickname)) {
                    // don't add a duplicate
                    weatherRVModelArrayList.add(new WeatherRVModel(nickname.getString("nickname")));
                    weatherRVFav.notifyDataSetChanged();
                    //getFavCities();
                } else {
                    // this shouldn't happen but could with the async nature of the application
                    Log.wtf("ERROR", "Contact already received: ");
                }

            }
            //inform observers of the change (setValue)
           // mContacts.setValue(list);
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
}
