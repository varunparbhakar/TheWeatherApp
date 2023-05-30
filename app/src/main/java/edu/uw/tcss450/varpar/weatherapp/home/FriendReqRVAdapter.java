package edu.uw.tcss450.varpar.weatherapp.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.uw.tcss450.varpar.weatherapp.R;

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
        private ImageView acceptButton;
        private ImageView declineButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTV = itemView.findViewById(R.id.idTVUser);
            acceptButton = itemView.findViewById(R.id.idIVAccept);
            declineButton = itemView.findViewById(R.id.idIVDecline);

            acceptButton.setOnClickListener(v -> {
                // you can get item position if you want to handle it
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // handle your click event here
                    acceptOnClick(position);
                }
            });

            declineButton.setOnClickListener(v -> {
                // you can get item position if you want to handle it
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // handle your click event here
                    declineOnClick(position);
                }
            });
        }
    }

    private void acceptOnClick(int position) {
        // handle accept button click
        FriendReqRVModel model = FriendReqRVModelArrayList.get(position);
    }

    private void declineOnClick(int position) {
        // handle decline button click
        FriendReqRVModel model = FriendReqRVModelArrayList.get(position);
    }
}
