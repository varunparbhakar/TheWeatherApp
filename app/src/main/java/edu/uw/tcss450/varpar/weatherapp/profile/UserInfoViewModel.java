package edu.uw.tcss450.varpar.weatherapp.profile;

import android.app.Application;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Arrays;
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

//        this.connectGetUserData();
        this.connectPostUserData();
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
            mFirstName = result.get("firstname").toString();
            mLastName = result.get("lastname").toString();
            mUsername = result.get("username").toString();
            mResponse.setValue(new JSONObject("{" +
                    "message:\"" + "PUT SUCCESS" +
                    "\"}"));
        } catch (JSONException e) {
            Log.e("JSON Parse", "Unable to parse JSON Fields");
            Log.e("JSON Parse", e.getMessage());
            Log.e("JSON Parse", e.toString());
            Log.e("JSON Parse", Arrays.toString(e.getStackTrace()));
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

    /* POST request. */
    public void connectPostUserData() {
        String url = "https://theweatherapp.herokuapp.com/info";

        JSONObject body = new JSONObject();
        try {
            body.put("email", this.mEmail);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.POST, //HTTP method
                url, //URL to go to
                body, //body, but no body in this request
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

    public void connectValidatePassword(final String oldPassword, final String newPassword) {
        String url = "https://theweatherapp.herokuapp.com/infotemp"; //TODO wrong, what creds

        JSONObject body = new JSONObject();
        try {
            body.put("oldpassword", oldPassword);
            body.put("newpassword", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.PUT,
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
