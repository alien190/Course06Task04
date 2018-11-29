package com.example.alien.course06task04;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongViewHolder> {

    private List<Song> mItems = new ArrayList<>();
    private IOnItemClickListener mOnItemClickListener;

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.li_song, viewGroup, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder songViewHolder, int i) {
        songViewHolder.bind(mItems.get(i), mOnItemClickListener);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addItem(Song song) {
        if (song != null) {
            mItems.add(song);
            notifyItemInserted(mItems.size() - 1);
        }
    }

    public void setOnItemClickListener(IOnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public int getIndex(Song song) {
        return mItems.indexOf(song);
    }

    public Song getItem(int index) {
        if (index >= 0 && index < mItems.size()) {
            return mItems.get(index);
        } else {
            throw new IllegalArgumentException("index out of bounds");
        }
    }

    public interface IOnItemClickListener {
        void onItemClick(Song song);
    }
}
