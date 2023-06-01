package edu.uw.tcss450.varpar.weatherapp.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentHomeBinding;
import edu.uw.tcss450.varpar.weatherapp.model.UserInfoViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private static final int PERMISSION_CODE = 1;
    FragmentHomeBinding mBinding;
    private TextView locationTextView, tempTV, conditionTV;
    private ImageView iconIV;
    private ArrayList<FriendReqRVModel> friendReqRVModelArrayList;
    private FriendReqRVAdapter friendReqRVAdapter;
    private RecyclerView FriendReqRV;
    private UserInfoViewModel mUserModel;

    public void onChatPreviewClicked(View view) {
        // Navigate to the messages tab
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.chat_user);
//        mBinding.frameLayout2.setVisibility(View.GONE);
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

        FriendReqRV = getView().findViewById(R.id.idRVIncomingFR);
        friendReqRVModelArrayList = new ArrayList<>();
        friendReqRVAdapter = new FriendReqRVAdapter(getActivity(), friendReqRVModelArrayList);
        FriendReqRV.setAdapter(friendReqRVAdapter);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }

        getWeatherInfo();

        super.onViewCreated(view, savedInstanceState);
        mUserModel = new ViewModelProvider(getActivity()).get(UserInfoViewModel.class);
        String welcomeText = getText(R.string.home_fragment_welcome) + " " + mUserModel.getFirstName();
        mBinding.textGreeting.setText(welcomeText);

        // Get the friend requests and display them
        getFriendsRequests();
        getAcceptFriendRequests();
//        getRemoveFriendRequests();

        //call a method that pulls friend requests from the SQL data
        // TODO: work on this dummy data and make it real data!!!!!!!!!!
        for (int i = 0; i < 5; i++) {
            String username = "Random Person " + i;
            friendReqRVModelArrayList.add(new FriendReqRVModel(username));
            //day++;
        }
        friendReqRVAdapter.notifyDataSetChanged();
    }

    private void getWeatherInfo() {
        String URL = "http://api.weatherapi.com/v1/forecast.json?key=9953bd3e0e9448fba21212344231405 &q=Tacoma&days=5&aqi=no&alerts=no";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                response -> {
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
                },
                error -> Toast.makeText(getActivity(), "Please enter valid city name...", Toast.LENGTH_SHORT).show());
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Get the friend requests from the database
     *
     * @return
     * @throws JSONException
     * @throws RuntimeException
     * @throws VolleyError
     */
    public void getFriendsRequests() {
        String URL = "https://theweatherapp.herokuapp.com/contacts/getrequests";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

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

    private void handleError(final VolleyError error) {
        Log.wtf("ERROR", error.getMessage());
        if (Objects.isNull(error.networkResponse)) {
            Log.e("NETWORK ERROR", error.getMessage());
        } else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.e("CLIENT ERROR",
                    error.networkResponse.statusCode + " " + data);
        }
    }

    private void handleResult(final JSONObject response) {
        Log.wtf("RESULT", response.toString());
        try {
            JSONArray friendRequestsArray = response.getJSONArray("friendRequests");

            for (int i = 0; i < friendRequestsArray.length(); i++) {
                JSONObject friendRequestObject = friendRequestsArray.getJSONObject(i);
                String friendName = friendRequestObject.getString("username");
                String idName = friendRequestObject.getString("memberid");  // Get the sender's name
//                    String senderName = friendRequestObject.getString("senderName");  // Get the sender's name
//                    friendReqRVModelArrayList.add(new FriendReqRVModel());  // Include the sender's name when creating a new FriendReqRVModel
            }

            // Notify the adapter that the data has changed
            friendReqRVAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void getAcceptFriendRequests() {
        String URL = "https://theweatherapp.herokuapp.com/contacts/acceptfriendrequest/";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

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

    public void getRemoveFriendRequests() {
        String URL = "https://theweatherapp.herokuapp.com/contacts/remove/";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        Request request = new JsonObjectRequest(Request.Method.POST, URL, null,
                response -> {

                    try {
                        JSONArray friendRequestsArray = response.getJSONArray("friendRequests");

                        for (int i = 0; i < friendRequestsArray.length(); i++) {
                            JSONObject friendRequestObject = friendRequestsArray.getJSONObject(i);
                            String friendName = friendRequestObject.getString("username");
                            String idName = friendRequestObject.getString("memberid");
//                            String senderName = friendRequestObject.getString("senderName");  // Get the sender's name
//                            friendReqRVModelArrayList.add(new FriendReqRVModel(senderName));  // Include the sender's name when creating a new FriendReqRVModel
                        }

                        // Notify the adapter that the data has changed
                        friendReqRVAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> Toast.makeText(getActivity(), "Failed to get friend requests...", Toast.LENGTH_SHORT).show());

        requestQueue.add(request);
    }
}