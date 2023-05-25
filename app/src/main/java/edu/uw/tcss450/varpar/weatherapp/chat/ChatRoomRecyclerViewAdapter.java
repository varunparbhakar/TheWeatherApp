package edu.uw.tcss450.varpar.weatherapp.chat;

import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.shape.CornerFamily;

import java.util.List;

import edu.uw.tcss450.varpar.weatherapp.R;
import edu.uw.tcss450.varpar.weatherapp.databinding.FragmentChatRoomMessageCardBinding;

public class ChatRoomRecyclerViewAdapter extends RecyclerView.Adapter<ChatRoomRecyclerViewAdapter.ChatRoomViewHolder> {

    //Store all of the blogs to present
    private final List<ChatRoomMessage> mMessages;
    private final String mEmail;

//    //Store the expanded state for each List item, true -> expanded, false -> not
//    private final Map<ChatRoomMessage, Boolean> mExpandedFlags;

    public ChatRoomRecyclerViewAdapter(List<ChatRoomMessage> items, String email) {
        this.mMessages = items;
        this.mEmail = email;
//        mExpandedFlags = mMessages.stream()
//                .collect(Collectors.toMap(Function.identity(), blog -> false));

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
        public final View mView;
        public FragmentChatRoomMessageCardBinding binding;
        private ChatRoomMessage mChat;

        public ChatRoomViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentChatRoomMessageCardBinding.bind(view);
//            binding.buittonMore.setOnClickListener(this::handleMoreOrLess);
        }

//        /**
//         * When the button is clicked in the more state, expand the card to display
//         * the blog preview and switch the icon to the less state.  When the button
//         * is clicked in the less state, shrink the card and switch the icon to the
//         * more state.
//         * @param button the button that was clicked
//         */
//        private void handleMoreOrLess(final View button) {
//            mExpandedFlags.put(mChat, !mExpandedFlags.get(mChat));
//        }

//        /**
//         * Helper used to determine if the preview should be displayed or not.
//         */
//        private void displayPreview() {
//            if (mExpandedFlags.get(mChat)) {
//                binding.textPreview.setVisibility(View.VISIBLE);
//                binding.buittonMore.setImageIcon(
//                        Icon.createWithResource(
//                                mView.getContext(),
//                                R.drawable.ic_less_grey_24dp));
//            } else {
//                binding.textPreview.setVisibility(View.GONE);
//                binding.buittonMore.setImageIcon(
//                        Icon.createWithResource(
//                                mView.getContext(),
//                                R.drawable.ic_more_grey_24dp));
//            }
//        }

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
                                .setTopLeftCorner(CornerFamily.ROUNDED,standard * 2)
                                .setBottomLeftCorner(CornerFamily.ROUNDED,standard * 2)
                                .setBottomRightCornerSize(0)
                                .setTopRightCornerSize(0)
                                .build()
                );

                card.requestLayout();
            } else {
                //This message is from another user. Format it as such
                binding.textChatRoomMessage.setText(
                        message.getSender() + ": " + message.getMessage()
                );
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
                                .setTopRightCorner(CornerFamily.ROUNDED,standard * 2)
                                .setBottomRightCorner(CornerFamily.ROUNDED,standard * 2)
                                .setBottomLeftCornerSize(0)
                                .setTopLeftCornerSize(0)
                                .build());
                card.requestLayout();
            }
        }
    }

}
