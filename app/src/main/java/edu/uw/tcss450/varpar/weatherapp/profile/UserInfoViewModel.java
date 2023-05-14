package edu.uw.tcss450.varpar.weatherapp.profile;

import android.app.Application;
import android.content.Context;
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

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.io.RequestQueueSingleton;

public class UserInfoViewModel extends AndroidViewModel {

    private String mEmail;
    private String mJwt;
    private String mFirstName;
    private String mLastName;
    private String mUsername;
    private MutableLiveData<JSONObject> mResponse;

    public  UserInfoViewModel(@NonNull Application application) {

        super(application);
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());

    }

    public void setJSON(JSONObject json) {
        mResponse.setValue(json);
        mEmail = getInfoJson("email");
        mJwt = getInfoJson("token");
        mFirstName = getInfoJson("firstname");
        mLastName = getInfoJson("lastname");
        mUsername = getInfoJson("username");
    }

    public String getInfoJson(String key) {
        String info = "";
        try {
            info = mResponse.getValue().getString(key);
        } catch (Exception e) {
            System.out.println("THERE WAS AN ERROR WITH THE JSON OBJECT");
            e.printStackTrace();
        }
        return info;
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

    public String getEmail() {
        return mEmail;
    }

    private void handleError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            try {
                mResponse.setValue(new JSONObject("{" +
                        "error:\"" + error.getMessage() +
                        "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError, While fetching user information");
            }
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset())
                    .replace('\"', '\'');
            try {
//                JSONObject response = new JSONObject();
//                response.put("code", error.networkResponse.statusCode);
//                response.put("data", new JSONObject(data));
//                mResponse.setValue(response);
                mResponse.setValue(new JSONObject("{" +
                        "code:" + error.networkResponse.statusCode +
                        ", data:\"" + data +
                        "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
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

    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        mResponse = new MutableLiveData<>(); //TODO preventing firing of old event
        mResponse.observe(owner, observer);
    }

    //TODO preventing firing of old event
    public void removeResponseObserver(@NonNull Observer<? super JSONObject> observer) {
        mResponse.removeObserver(observer);
    }

}
