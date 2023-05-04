package edu.uw.tcss450.varpar.weatherapp.weather;

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
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Objects;

public class WeatherViewModel extends AndroidViewModel {
    private MutableLiveData<JSONObject> mResponse;

    public WeatherViewModel(@NonNull Application application) {

        super(application);
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
    }
//    public void addResponseObserver(@NonNull LifecycleOwner owner,
//                                    @NonNull Observer<? super JSONObject> observer) {
//        mResponse.observe(owner, observer);
//    }
//    private void handleResult(final JSONObject result) {
//        mResponse.setValue(result);
//    }
//    private void handleError(final VolleyError error) {
//        if (Objects.isNull(error.networkResponse)) {
//            try {
//                mResponse.setValue(new JSONObject("{" +
//                        "error:\"" + error.getMessage() +
//                        "\"}"));
//            } catch (JSONException e) {
//                Log.e("JSON PARSE", "JSON Parse Error in handleError");
//            }
//        }
//        else {
//            String data = new String(error.networkResponse.data, Charset.defaultCharset())
//                    .replace('\"', '\'');
//            try {
//                mResponse.setValue(new JSONObject("{" +
//                        "code:" + error.networkResponse.statusCode +
//                        ", data:\"" + data +
//                        "\"}"));
//            } catch (JSONException e) {
//                Log.e("JSON PARSE", "JSON Parse Error in handleError");
//            }
//        }
//    }
//    public void connectGet(final String city) {
//        String url = "https://api.weatherbit.io/v2.0/current?cities=" + city + "&key=3f9754187e0b4f37b78c04f495a6cdfb";
//        Request request = new JsonObjectRequest(
//                Request.Method.GET,
//                url,
//                null, //no body for this get request
//                mResponse::setValue,
//                this::handleError);
//        request.setRetryPolicy(new DefaultRetryPolicy(
//                10_000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        //Instantiate the RequestQueue and add the request to the queue
//        Volley.newRequestQueue(getApplication().getApplicationContext())
//                .add(request);
//    }

}
