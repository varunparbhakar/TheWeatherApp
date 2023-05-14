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

public class UserInfoViewModel extends AndroidViewModel {

    private String mEmail;
    private String mJwt;
    private MutableLiveData<JSONObject> mResponse;

    public  UserInfoViewModel(@NonNull Application application) {

        super(application);
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());

    }

    public JSONObject getJSON() {
        return mResponse.getValue();
    }
    public void setJSON(JSONObject json) {
        mResponse.setValue(json);

    }

    /**
     * For this method we take the json object and ask of a key, this method
     * will then take the key and return the corresponding value
     * @param key
     * @return
     */
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

//    public String getInfo(String key) {
//        connect(getEmail());
//        Log.i("CONNECTED ONLINE", "RETURN M-RESPONSE");
//
//        String info;
//        try {
//            info = mResponse.getValue().getString(key);
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//
//        return info;
//    }
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
                JSONObject response = new JSONObject();
                response.put("code", error.networkResponse.statusCode);
                response.put("data", new JSONObject(data));
                mResponse.setValue(response);
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
    }
    //No longer used
    public void connect(final String email) {
        String url = getApplication().getResources().getString(R.string.url)+ "info";
        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
               response -> {
                    mResponse.setValue(response);
               },
                this::handleError)
//            {
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> headers = new HashMap<>();
//                // add headers <key,value>
//                headers.put("Authorization", getmJwt());
//                return headers;
//            }}
        ;
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        Volley.newRequestQueue(getApplication().getApplicationContext()).add(request);
        System.out.println(mResponse.toString());
    }

    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        mResponse.observe(owner, observer);
    }


//    public static class UserInfoViewModelFactory implements ViewModelProvider.Factory {
//
//        private final String email;
//        private final String jwt;
//        public UserInfoViewModelFactory(String email, String jwt, Context context) {
//            this.email = email;
//            this.jwt = jwt;
//            this.context = context;
//        }
//
//        @NonNull
//        @Override
//        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
//            if (modelClass == UserInfoViewModel.class) {
//                return (T) new UserInfoViewModel(contextemail, jwt);
//            }
//            throw new IllegalArgumentException(
//                    "Argument must be: " + UserInfoViewModel.class);
//        }
//
//    }


}
