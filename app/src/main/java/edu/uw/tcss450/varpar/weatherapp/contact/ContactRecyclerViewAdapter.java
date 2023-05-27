package edu.uw.tcss450.varpar.weatherapp.contact;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentContactCardBinding;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentContactListBinding;
import edu.uw.tcss450.varpar.weatherapp.io.RequestQueueSingleton;
import edu.uw.tcss450.varpar.weatherapp.model.UserInfoViewModel;

public class ContactRecyclerViewAdapter extends RecyclerView.Adapter<ContactRecyclerViewAdapter.ContactViewHolder> {

    /** Store all of the Contacts to present. */
    private final List<Contact> mContacts;
    /** Store copy all of the Contacts to present for safe search. */
    private List<Contact> mContactsCopy;

    public String mMemberID;

    public ContactRecyclerViewAdapter(List<Contact> items) {
        this.mContacts = items;
        this.mContactsCopy = new ArrayList<>();
        this.mContactsCopy.addAll(items);
    }

    public void filter(String text) {
        mContacts.clear();
        if(text.isEmpty()){
            mContacts.addAll(mContactsCopy);
        } else{
            text = text.toLowerCase();
            for(Contact item: mContactsCopy){
                if(item.getUsername().toLowerCase().contains(text)){
                    mContacts.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_contact_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.setContact(mContacts.get(position));
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    /**
     * Objects from this class represent an Individual row View from the List
     * of rows in the Contact Recycler View.
     */
    public class ContactViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public FragmentContactCardBinding binding;
        private Contact mContact;

        public ContactViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentContactCardBinding.bind(view);
        }

        private void deleteUser(final View button) {

        }

        void setContact(final Contact contact) {
            mContact = contact;
            binding.textContactUser.setText(contact.getUsername());
            binding.deleteUser.setOnClickListener(this::deleteUser);
        }
    }
}
