package edu.uw.tcss450.varpar.weatherapp.chat;

import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.shape.CornerFamily;

import java.util.List;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentChatRoomMessageCardBinding;

/**
 * Recycler view adapter for chatrooms to hold and display messages.
 */
public class ChatRoomRecyclerViewAdapter extends RecyclerView.Adapter<ChatRoomRecyclerViewAdapter.ChatRoomViewHolder> {

    /** List of messages in the chatroom. */
    private final List<ChatRoomMessage> mMessages;

    /** User email. */
    private final String mEmail;

    /**
     * Constructor for view adapter.
     * @param items messages to display.
     * @param email email of user.
     */
    public ChatRoomRecyclerViewAdapter(final List<ChatRoomMessage> items, final String email) {
        this.mMessages = items;
        this.mEmail = email;
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatRoomViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_chat_room_message_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position) {
        holder.setMessage(mMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    /**
     * Objects from this class represent an Individual row View from the List
     * of rows in the Chat Recycler View.
     */
    public class ChatRoomViewHolder extends RecyclerView.ViewHolder {

        /** View of holder. */
        private final View mView;

        /** Binding of items in view. */
        public FragmentChatRoomMessageCardBinding binding;

        /**
         * Constructor to the holder view.
         * @param view view of the holder.
         */
        public ChatRoomViewHolder(final View view) {
            super(view);
            mView = view;
            binding = FragmentChatRoomMessageCardBinding.bind(view);
        }

        /**
         * Logic for each message present in the chatroom.
         * @param message message to display.
         */
        void setMessage(final ChatRoomMessage message) {
            final Resources res = mView.getContext().getResources();
            final MaterialCardView card = binding.cardRoot;

            int standard = (int) res.getDimension(R.dimen.chat_margin);
            int extended = (int) res.getDimension(R.dimen.chat_margin_sided);

            if (mEmail.equals(message.getSender())) {
                //This message is from the user. Format it as such
                binding.textChatRoomMessage.setText(message.getMessage());
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) card.getLayoutParams();

                //Set the left margin
                layoutParams.setMargins(extended, standard, standard, standard);
                // Set this View to the right (end) side
                ((FrameLayout.LayoutParams) card.getLayoutParams()).gravity =
                        Gravity.END;

                card.setCardBackgroundColor(
                    ColorUtils.setAlphaComponent(
                            res.getColor(R.color.colorPrimary, null),
                            16
                    )
                );
                binding.textChatRoomMessage.setTextColor(
                        res.getColor(R.color.black, null)
                );

                card.setStrokeWidth(standard / 5);
                card.setStrokeColor(
                    ColorUtils.setAlphaComponent(
                        res.getColor(R.color.colorPrimary, null),
                        200
                    )
                );

                //Round the corners on the left side
                card.setShapeAppearanceModel(
                        card.getShapeAppearanceModel()
                                .toBuilder()
                                .setTopLeftCorner(CornerFamily.ROUNDED, standard * 2)
                                .setBottomLeftCorner(CornerFamily.ROUNDED, standard * 2)
                                .setBottomRightCornerSize(0)
                                .setTopRightCornerSize(0)
                                .build()
                );

                card.requestLayout();
            } else {
                //This message is from another user. Format it as such
                binding.textChatRoomMessage.setText(message.getSender() + ": " + message.getMessage());
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) card.getLayoutParams();

                //Set the right margin
                layoutParams.setMargins(standard, standard, extended, standard);
                // Set this View to the left (start) side
                ((FrameLayout.LayoutParams) card.getLayoutParams()).gravity =
                        Gravity.START;

                card.setCardBackgroundColor(
                    ColorUtils.setAlphaComponent(
                        res.getColor(R.color.colorAccent, null),
                        16
                    )
                );

                card.setStrokeWidth(standard / 5);
                card.setStrokeColor(
                    ColorUtils.setAlphaComponent(
                        res.getColor(R.color.colorAccent, null),
                        200
                    )
                );

                binding.textChatRoomMessage.setTextColor(
                        res.getColor(R.color.black, null)
                );

                //Round the corners on the right side
                card.setShapeAppearanceModel(
                        card.getShapeAppearanceModel()
                                .toBuilder()
                                .setTopRightCorner(CornerFamily.ROUNDED, standard * 2)
                                .setBottomRightCorner(CornerFamily.ROUNDED, standard * 2)
                                .setBottomLeftCornerSize(0)
                                .setTopLeftCornerSize(0)
                                .build());
                card.requestLayout();
            }
        }
    }
}
