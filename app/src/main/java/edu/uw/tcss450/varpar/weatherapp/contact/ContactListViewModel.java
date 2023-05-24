package edu.uw.tcss450.varpar.weatherapp.contact;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.chat.ChatRoomMessage;
import edu.uw.tcss450.varpar.weatherapp.io.RequestQueueSingleton;

public class ContactListViewModel extends AndroidViewModel {

    /**
     * A List of Contacts.
     */
    private MutableLiveData<List<Contact>> mContacts;

    /** User's jwt. */
    private String mJwt;

    public ContactListViewModel(@NonNull Application application) {
        super(application);
        mContacts = new MutableLiveData<>();
        mContacts.setValue(new ArrayList<>());
    }

    public void setJwt(String mJwt) {
        this.mJwt = mJwt;
    }

    /**
     * Register as an observer to listen to contact list.
     * @param owner the fragments lifecycle owner
     * @param observer the observer
     */
    public void addContactObserver(@NonNull LifecycleOwner owner,
                                   @NonNull Observer<? super List<Contact>> observer) {
        mContacts.observe(owner, observer);
    }

    /**
     * Makes a request to the web service to get the contacts.
     */
    public void getContacts() {
        String url = getApplication().getResources().getString(R.string.url) +
                "contacts/getfriends";

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
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
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);
    }

    /**
     * When a contact is received externally to this ViewModel, add it with this method.
     * @param cont Contact being added.
     */
    public void addContact(final Contact cont) {
        mContacts.getValue().add(cont);
        Collections.sort(mContacts.getValue());
    }

    private void handleSuccess(final JSONObject response) {
        List<Contact> list = new ArrayList<>();

        //TODO: how to eat from endpoint
//        if (!response.has("memberId")) {
//            throw new IllegalStateException("Unexpected response in ContactListViewModel: " + response);
//        }
        try {
            JSONArray contacts = response.getJSONArray("rows");
            for(int i = 0; i < contacts.length(); i++) {
                JSONObject message = contacts.getJSONObject(i);
                Contact c = new Contact.Builder(message.get("firstname").toString()).build();
                if (!list.contains(c)) {
                    // don't add a duplicate
                    list.add(c);
                } else {
                    // this shouldn't happen but could with the async nature of the application
                    Log.wtf("ERROR", "Contact already received: " + c.getUser());
                }

            }
            //inform observers of the change (setValue)
            mContacts.setValue(list);
        }catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ContactListViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
    }

    private void handleError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            Log.e("NETWORK ERROR", error.getMessage());
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.e("CLIENT ERROR",
                    error.networkResponse.statusCode +
                            " " +
                            data);
        }
    }

}

