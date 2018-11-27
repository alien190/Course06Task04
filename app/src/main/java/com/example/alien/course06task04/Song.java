package com.example.alien.course06task04;

public class Song {
    private String mArtist;
    private String mTitle;
    private String mDiration;
    private String mPath;

    public Song(String artist, String title, String diration, String path) {
        mArtist = artist;
        mTitle = title;
        mDiration = diration;
        mPath = path;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        mArtist = artist;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDiration() {
        return mDiration;
    }

    public void setDiration(String diration) {
        mDiration = diration;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }
}
