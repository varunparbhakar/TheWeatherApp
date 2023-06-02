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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.home.FriendReqRVAdapter;
import edu.uw.tcss450.varpar.weatherapp.io.RequestQueueSingleton;

/**
 * Model logic for Contacts, contains server calls.
 * @author Nathan Brown, James Deal
 */
public class ContactListViewModel extends AndroidViewModel {

    /** A List of Contacts. */
    private MutableLiveData<List<Contact>> mContacts;

    /** User's jwt. */
    private String mJwt;

    /** User's memberID. */
    private String mMemberID;

    /** Network responses, for observer. */
    private MutableLiveData<JSONObject> mResponse;

    public ContactListViewModel(@NonNull Application application) {
        super(application);
        mContacts = new MutableLiveData<>();
        mContacts.setValue(new ArrayList<>());
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
     * Obtain access to member JWT from UIVM in Fragment.
     * @param Jwt member JWT.
     */
    public void setJwt(final String Jwt) {
        this.mJwt = Jwt;
    }

    /**
     * Obtain access to member ID from UIVM in Fragment.
     * @param MemberID member ID.
     */
    public void setMemberID(final String MemberID) {
        this.mMemberID = MemberID;
    }

    /**
     * Give access to list of contacts.
     * @return list of contacts.
     */
    public List<Contact> getContactList() {
        return mContacts.getValue();
    }

    /**
     * Returns if list of contacts is empty.
     * @return true if empty.
     */
    public boolean isEmpty() {
        return mContacts.getValue().isEmpty();
    }

    /**
     * Register as an observer to listen to contact list.
     * @param owner the fragments lifecycle owner
     * @param observer the observer
     */
    public void addContactListObserver(@NonNull LifecycleOwner owner,
                                   @NonNull Observer<? super List<Contact>> observer) {
        mContacts.observe(owner, observer);
    }

    /**
     * Makes a request to the web service to get the contacts.
     */
    public void getContacts() {
        String url = getApplication().getResources().getString(R.string.url)
                + "contacts/getfriends/" + mMemberID;

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
     * Retrieve JSONObject of contacts and parse into list, notify observers.
     * @param response JSONObject of contacts.
     */
    private void handleSuccess(final JSONObject response) {
        List<Contact> list = new ArrayList<>();

        try {
            if (response.has("message") && response.get("message").equals("no current requests")) {
                mContacts.setValue(list);
                return;
            }
        } catch (JSONException e) {
            Log.wtf("ERROR", "unknown error");
        }

        try {
            JSONArray contacts = response.getJSONArray("rows");
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject message = contacts.getJSONObject(i);
                Contact c = new Contact.Builder(message.get("username").toString(), message.getString("memberid")).build();
                if (!list.contains(c)) {
                    // don't add a duplicate
                    list.add(c);
                } else {
                    // this shouldn't happen but could with the async nature of the application
                    Log.wtf("ERROR", "Contact already received: " + c.getUsername());
                }

            }
            //inform observers of the change (setValue)
            mContacts.setValue(list);
        } catch (JSONException e) {
            Log.wtf("JSON PARSE ERROR", "Found in handle Success ContactListViewModel");
            Log.wtf("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
    }

    /**
     * Error occurred in server communication to gain contacts.
     * @param error error that happened.
     */
    private void handleError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            Log.wtf("NETWORK ERROR", error.getMessage());
        } else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.wtf("CLIENT ERROR",
                    error.networkResponse.statusCode + " " + data);
        }
    }

    /**
     * Connect to server to add new contact.
     * @param username Contact being added.
     */
    public void connectAddContact(final String username) {
        String url = getApplication().getResources().getString(R.string.url) + "contacts/sendfriendrequest/";

        JSONObject body = new JSONObject();
        try {
            body.put("sender", mMemberID);
            body.put("receiver", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                this::postHandleSuccess,
                this::postHandleError) {

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
     * Success handler for adding contact.
     * @param response server response.
     */
    private void postHandleSuccess(final JSONObject response) {
        try { //note what type of connection happened
            response.put("type", "post");
        } catch (JSONException e) {
            Log.wtf("JSON Error", e.getMessage());
        }
        mResponse.setValue(response);
    }

    /**
     * Error handler for adding contact.
     * @param error server response.
     */
    private void postHandleError(final VolleyError error) {
        JSONObject resp = new JSONObject();
        try { //note what type of connection happened
            resp.put("type", "post");
        } catch (JSONException e) {
            Log.wtf("JSON Error", e.getMessage());
        }

        if (Objects.isNull(error.networkResponse)) { //server error?
            Log.wtf("NETWORK ERROR", error.getMessage());
            try {
                resp.put("message", "Network Error");
            } catch (JSONException e) {
                Log.wtf("JSON Error", e.getMessage());
            }
        } else { //client error?
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.wtf("CLIENT ERROR",
                    error.networkResponse.statusCode + " " + data);
            try {
                JSONObject dat = new JSONObject(new String(error.networkResponse.data, Charset.defaultCharset()));
                resp.put("message", dat.getString("message"));
            } catch (JSONException e) {
                Log.wtf("JSON Error", e.getMessage());
            }
        }

        //notify
        mResponse.setValue(resp);
    }

    /**
     * Connect to server to delete contact.
     * @param memberID memberID of contact to delete
     */
    public void connectDeleteContact(final String memberID) {
        String url = getApplication().getResources().getString(R.string.url) + "contacts/remove/";

        JSONObject body = new JSONObject();
        try {
            body.put("user", mMemberID);
            body.put("friend", memberID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                this::deleteHandleSuccess,
                this::deleteHandleError) {

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
     * Success handler for deleting contact.
     * @param response server response.
     */
    private void deleteHandleSuccess(final JSONObject response) {
        try { //note what type of connection happened
            response.put("type", "delete");
            response.put("success", "true");
        } catch (JSONException e) {
            Log.wtf("JSON Error", e.getMessage());
        }
        mResponse.setValue(response);
    }

    /**
     * Error handler for deleting contact.
     * @param error server response.
     */
    private void deleteHandleError(final VolleyError error) {
        JSONObject resp = new JSONObject();
        try { //note what type of connection happened
            resp.put("type", "delete");
            resp.put("success", "false");
        } catch (JSONException e) {
            Log.wtf("JSON Error", e.getMessage());
        }

        if (Objects.isNull(error.networkResponse)) { //server error?
            Log.wtf("NETWORK ERROR", error.getMessage());
            try {
                resp.put("message", "Network Error");
            } catch (JSONException e) {
                Log.wtf("JSON Error", e.getMessage());
            }
        } else { //client error?
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.wtf("CLIENT ERROR",
                    error.networkResponse.statusCode + " " + data);
            try {
                JSONObject dat = new JSONObject(new String(error.networkResponse.data, Charset.defaultCharset()));
                resp.put("message", dat.getString("message"));
            } catch (JSONException e) {
                Log.wtf("JSON Error", e.getMessage());
            }
        }

        //notify
        mResponse.setValue(resp);
    }

    /**
     * Connect to server to initiate chat with user.
     * @param memberID memberID to chat with
     */
    public void connectAddContactChat(final String memberID) {
        String url = getApplication().getResources().getString(R.string.url) + "chats/createchat";

        JSONObject body = new JSONObject();
        try {
            body.put("userone", mMemberID);
            body.put("usertwo", memberID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                this::postChatHandleSuccess,
                this::postChatHandleError) {

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
     * Success handler for adding chat with contact.
     * @param response server response.
     */
    private void postChatHandleSuccess(final JSONObject response) {
        try { //note what type of connection happened
            response.put("type", "chat");
            response.put("success", "true");
        } catch (JSONException e) {
            Log.wtf("JSON Error", e.getMessage());
        }
        mResponse.setValue(response);
    }

    /**
     * Error handler for adding chat with contact.
     * @param error server response.
     */
    private void postChatHandleError(final VolleyError error) {
        JSONObject resp = new JSONObject();
        try { //note what type of connection happened
            resp.put("type", "chat");
            resp.put("success", "false");
        } catch (JSONException e) {
            Log.wtf("JSON Error", e.getMessage());
        }

        if (Objects.isNull(error.networkResponse)) { //server error?
            Log.wtf("NETWORK ERROR", error.getMessage());
            try {
                resp.put("message", "Network Error");
            } catch (JSONException e) {
                Log.wtf("JSON Error", e.getMessage());
            }
        } else { //client error?
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.wtf("CLIENT ERROR",
                    error.networkResponse.statusCode + " " + data);
            try {
                JSONObject dat = new JSONObject(new String(error.networkResponse.data, Charset.defaultCharset()));
                resp.put("message", dat.getString("message"));
            } catch (JSONException e) {
                Log.wtf("JSON Error", e.getMessage());
            }
        }

        //notify
        mResponse.setValue(resp);
    }
}
