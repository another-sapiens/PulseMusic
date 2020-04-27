package com.hardcodecoder.pulsemusic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.hardcodecoder.pulsemusic.R;
import com.hardcodecoder.pulsemusic.interfaces.ItemClickListener;
import com.hardcodecoder.pulsemusic.interfaces.ItemTouchHelperAdapter;
import com.hardcodecoder.pulsemusic.interfaces.ItemTouchHelperViewHolder;
import com.hardcodecoder.pulsemusic.interfaces.SimpleGestureCallback;

import java.util.List;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CardsSVH> implements ItemTouchHelperAdapter {

    private List<String> mPlaylistNames;
    private ItemClickListener.Cards mListener;
    private LayoutInflater mInflater;
    private SimpleGestureCallback mCallback;

    public CardsAdapter(List<String> playlistNames, LayoutInflater inflater, ItemClickListener.Cards mListener, @Nullable SimpleGestureCallback callback) {
        this.mPlaylistNames = playlistNames;
        this.mInflater = inflater;
        this.mListener = mListener;
        this.mCallback = callback;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
    }

    @Override
    public void onItemDismiss(int position) {
        if (null != mCallback)
            mCallback.onItemDismissed(position);
    }

    @NonNull
    @Override
    public CardsSVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CardsSVH(mInflater.inflate(R.layout.rv_playlist_card_item, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CardsSVH holder, int position) {
        holder.updateView(mPlaylistNames.get(position));
    }

    @Override
    public int getItemCount() {
        if (mPlaylistNames != null)
            return mPlaylistNames.size();
        else return 0;
    }

    static class CardsSVH extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        private TextView title;

        CardsSVH(@NonNull View itemView, ItemClickListener.Cards listener) {
            super(itemView);
            title = itemView.findViewById(R.id.playlist_title);

            itemView.findViewById(R.id.edit_btn).setOnClickListener(v -> listener.onEdit(getAdapterPosition()));

            itemView.setOnClickListener(v -> listener.onItemClick(getAdapterPosition()));
        }

        void updateView(String s) {
            title.setText(s);
            if (getAdapterPosition() == 0)
                itemView.findViewById(R.id.edit_btn).setVisibility(View.GONE);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackground(itemView.getContext().getDrawable(R.drawable.active_item_background));
        }

        @Override
        public void onItemClear() {
            itemView.setBackground(itemView.getContext().getDrawable(android.R.color.transparent));
        }
    }
}
