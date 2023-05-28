package edu.uw.tcss450.varpar.weatherapp.model;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import edu.uw.tcss450.varpar.weatherapp.io.RequestQueueSingleton;

/**
 * User information state, password changing network calls.
 * @author James Deal
 */
public class UserInfoViewModel extends AndroidViewModel {

    /** User email. */
    private String mEmail;

    /** User jwt token. */
    private String mJwt;

    /** User first name. */
    private String mFirstName;

    /** User last name. */
    private String mLastName;

    /** User username. */
    private String mUsername;

    /** User memberID. */
    private String mMemberID;

    /** Network responses, for observer. */
    private MutableLiveData<JSONObject> mResponse;

    /**
     * Default constructor.
     * @param application parent application.
     */
    public UserInfoViewModel(final @NonNull Application application) {
        super(application);
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
    }

    /**
     * Set initial values for user profile.
     * DO NOT USE AFTER MAIN ACTIVITY IS INITIALLY CREATED.
     * @param json user profile values.
     */
    public void setJSON(final JSONObject json) {
        mResponse.setValue(json);
        mEmail = getInfoJson("email");
        mJwt = getInfoJson("token");
        mFirstName = getInfoJson("firstname");
        mLastName = getInfoJson("lastname");
        mUsername = getInfoJson("username");
        mMemberID = getInfoJson("memberid");
    }

    /**
     * Take values from json based on key.
     * DO NOT USE AFTER MAIN ACTIVITY IS INITIALLY CREATED.
     * @param key key to check.
     * @return value contained.
     */
    private String getInfoJson(final String key) {
        String info = "";
        try {
            info = mResponse.getValue().getString(key);
        } catch (Exception e) {
            System.out.println("THERE WAS AN ERROR WITH THE JSON OBJECT");
            e.printStackTrace();
        }
        return info;
    }

    /**
     * Error handling for network connectivity.
     * @param error error given from network call.
     */
    private void handleError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            try {
                mResponse.setValue(new JSONObject("{"
                        + "error:\"" + error.getMessage()
                        + "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        } else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset())
                    .replace('\"', '\'');
            try {
                mResponse.setValue(new JSONObject("{"
                        + "code:" + error.networkResponse.statusCode
                        + ", data:\"" + data + "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
    }

    /**
     * Attempt change of user password.
     * @param oldPassword old password, to verify user.
     * @param newPassword new password to set.
     */
    public void connectValidatePassword(final String oldPassword, final String newPassword) {
        String url = "https://theweatherapp.herokuapp.com/infotemp"; //TODO wrong, what creds

        JSONObject body = new JSONObject();
        try {
            body.put("memberid", mMemberID);
            body.put("oldpassword", oldPassword);
            body.put("newpassword", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request =  new JsonObjectRequest(
                Request.Method.PUT,
                url,
                body, //JSON body for this get request
                mResponse::setValue,
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
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);
    }

    /**
     * Add observer to network responses.
     * @param owner Lifecycle parent.
     * @param observer UserInfoViewModel.class
     */
    public void addResponseObserver(final @NonNull LifecycleOwner owner,
                                    final @NonNull Observer<? super JSONObject> observer) {
        mResponse = new MutableLiveData<>();
        mResponse.observe(owner, observer);
    }

    /**
     * Remove observer to network responses.
     * @param observer UserInfoViewModel.class
     */
    public void removeResponseObserver(final @NonNull Observer<? super JSONObject> observer) {
        mResponse.removeObserver(observer);
    }

    /**
     * Get firstname.
     * @return firstname as string
     */
    public String getFirstName() {
        return mFirstName;
    }

    /**
     * Get lastname.
     * @return lastname as string
     */
    public String getLastName() {
        return mLastName;
    }

    /**
     * Get username.
     * @return username as string
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * Get email.
     * @return email as string
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Get User's JWT.
     * @return JWT as string
     */
    public String getJwt(){return mJwt;}

    /**
     * Get user's Member ID.
     * @return memberid as string
     */
    public String getMemberID() {
        return mMemberID;
    }
}
