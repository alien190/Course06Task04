package com.example.alien.course06task04;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class SongViewHolder extends RecyclerView.ViewHolder {
    private TextView mTitleTextView;
    private TextView mArtistTextView;
    private TextView mDurationTextView;
    private Song mSong;
    private SongAdapter.IOnItemClickListener mOnItemClickListener;

    public SongViewHolder(@NonNull View itemView) {
        super(itemView);
        mTitleTextView = itemView.findViewById(R.id.tv_title);
        mArtistTextView = itemView.findViewById(R.id.tv_artist);
        mDurationTextView = itemView.findViewById(R.id.tv_duration);
        itemView.setOnClickListener(v -> onClick());
    }

    public void bind(Song song, SongAdapter.IOnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        if (song != null) {
            mArtistTextView.setText(song.getArtist());
            mTitleTextView.setText(song.getTitle());
            mDurationTextView.setText(song.getDiration());
            mSong = song;
        }
    }

    private void onClick() {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(mSong);
        }
    }
}
