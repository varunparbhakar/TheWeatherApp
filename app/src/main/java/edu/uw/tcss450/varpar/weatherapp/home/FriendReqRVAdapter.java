package edu.uw.tcss450.varpar.weatherapp.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.weather.WeatherRVAdapter;
import edu.uw.tcss450.varpar.weatherapp.weather.WeatherRVModel;

public class FriendReqRVAdapter extends RecyclerView.Adapter<FriendReqRVAdapter.ViewHolder> {
    private Context context;
    private ArrayList<FriendReqRVModel> FriendReqRVModelArrayList;

    public FriendReqRVAdapter(Context context, ArrayList<FriendReqRVModel> FriendReqRVModelArrayList) {
        this.context = context;
        this.FriendReqRVModelArrayList = FriendReqRVModelArrayList;
    }

    @NonNull
    @Override
    public FriendReqRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_home_fr_card,parent,false);
        return new FriendReqRVAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendReqRVAdapter.ViewHolder holder, int position) {
        FriendReqRVModel model = FriendReqRVModelArrayList.get(position);
        holder.usernameTV.setText(model.getName());
    }

    @Override
    public int getItemCount() {
        return FriendReqRVModelArrayList.size();
    }
//    public static void updateAdapter() {
//        notifyDataSetChanged();
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView usernameTV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTV = itemView.findViewById(R.id.idTVUser);
        }
    }
}
