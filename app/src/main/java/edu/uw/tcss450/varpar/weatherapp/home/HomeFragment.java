package edu.uw.tcss450.varpar.weatherapp.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.chat.ChatListFragmentDirections;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentHomeBinding;
import edu.uw.tcss450.varpar.weatherapp.profile.UserInfoViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    FragmentHomeBinding mBinding;
    private TextView locationTextView, tempTV, conditionTV;
    private ImageView iconIV;

    public void onChatPreviewClicked(View view) {
        // Navigate to the messages tab
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.chat_user);
////        mBinding.frameLayout2.setVisibility(View.GONE);
//        mBinding.frameLayout2.setOnClickListener(
//                card -> {
//                    Navigation.findNavController().navigate(
//                            edu.uw.tcss450.varpar.weatherapp.chat.ChatListFragmentDirections.actionNavigationChatToChatRoom());
//                }
//        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentHomeBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        locationTextView = view.findViewById(R.id.location_text);
        tempTV = view.findViewById(R.id.temperature_text);
        conditionTV = view.findViewById(R.id.temp_forecast);
        iconIV = view.findViewById(R.id.forecast_image);

        getWeatherInfo();

        super.onViewCreated(view, savedInstanceState);
        UserInfoViewModel model = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);
        String welcomeText = getText(R.string.home_fragment_welcome) + " " + model.getFirstName();
        mBinding.textGreeting.setText(welcomeText);
    }

    private void getWeatherInfo() {
        String URL = "http://api.weatherapi.com/v1/forecast.json?key=9953bd3e0e9448fba21212344231405 &q=Tacoma&days=5&aqi=no&alerts=no";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                loadingPB.setVisibility(View.GONE);
//                homeRL.setVisibility(View.VISIBLE);

                try {
                    String cityName = response.getJSONObject("location").getString("name");
                    locationTextView.setText(cityName);
                    String temp = response.getJSONObject("current").getString("temp_f");
                    tempTV.setText(temp);
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(iconIV);
                    conditionTV.setText(condition);

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecast0 = forecastObj.getJSONArray("forecastday").getJSONObject(0);
//                    JSONArray hourArray = forecast0.getJSONArray("hour");

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        },
                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Please enter valid city name...", Toast.LENGTH_SHORT).show();

            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}