package com.example.alien.course06task04;

import android.app.Application;

public class App extends Application {
    private SongAdapter mSongAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        mSongAdapter = new SongAdapter();
    }

    public SongAdapter getSongAdapter() {
        return mSongAdapter;
    }

}
