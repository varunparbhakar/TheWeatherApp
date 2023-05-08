package edu.uw.tcss450.varpar.weatherapp.profile;

import android.app.Application;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import edu.uw.tcss450.varpar.weatherapp.io.RequestQueueSingleton;

public class UserInfoViewModel extends AndroidViewModel {

    private final String mEmail;
    private final String mJwt;
    private String mFirstName;
    private String mLastName;
    private String mUsername;
    private MutableLiveData<JSONObject> mResponse;

    private UserInfoViewModel(@NonNull Application application, String email, String jwt) {
        super(application);
        mEmail = email;
        mJwt = jwt;

        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());

        this.connectGetUserData();
    }

    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        mResponse.observe(owner, observer);
    }

    public String getEmail() {
        return mEmail;
    }

    public String getmJwt() {
        return mJwt;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getUsername() {
        return mUsername;
    }

    private void setUserData(final JSONObject result) {
        try {
            mFirstName = result.get("first").toString();
            mLastName = result.get("last").toString();
            mUsername = result.get("username").toString();
        } catch (JSONException e) {
            Log.e("JSON Parse", "Unable to parse JSON Fields");
        }
    }

    /* not successful connection. */
    private void handleError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            try {
                mResponse.setValue(new JSONObject("{" +
                        "error:\"" + error.getMessage() +
                        "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset())
                    .replace('\"', '\'');
            try {
                mResponse.setValue(new JSONObject("{" +
                        "code:" + error.networkResponse.statusCode +
                        ", data:\"" + data +
                        "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
    }

    /* GET request. */
    public void connectGetUserData() {
        String url = "https://theweatherapp.herokuapp.com/info"; //TODO wrong, what creds

        Request request = new JsonObjectRequest(
                Request.Method.GET, //HTTP method
                url, //URL to go to
                null, //body, but no body in this request
                this::setUserData, //what to do on success
                this::handleError); //what to do on error

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Instantiate the RequestQueue and add the request to the Queue
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);
    }

    public void connectValidatePassword(final String email, final String password) {
        String url = "https://theweatherapp.herokuapp.com/auth";
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                mResponse::setValue, //TODO let fragment know success
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                String credentials = email + ":" + password;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                headers.put("Authorization", auth);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);
    }

    public void connectPostPassword(final String newPassword) {
        String url = "https://theweatherapp.herokuapp.com/info"; //TODO wrong, what creds

        JSONObject body = new JSONObject();
        try {
            body.put("password", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body, //JSON body for this get request
                mResponse::setValue, //TODO change to let user know success
                this::handleError);

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);
    }

    public static class UserInfoViewModelFactory implements ViewModelProvider.Factory {

        private final String email;
        private final String jwt;
        private final Application app;

        public UserInfoViewModelFactory(@NonNull Application application, String email, String jwt) {
            this.email = email;
            this.jwt = jwt;
            this.app = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass == UserInfoViewModel.class) {
                return (T) new UserInfoViewModel(app, email, jwt);
            }
            throw new IllegalArgumentException(
                    "Argument must be: " + UserInfoViewModel.class);
        }
    }

}
