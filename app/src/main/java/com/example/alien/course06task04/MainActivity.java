package com.example.alien.course06task04;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements SongAdapter.IOnItemClickListener {

    private static final int PERMISSION_REQUEST_CODE = 11;
    private RecyclerView mRecyclerView;
    private SongAdapter mSongAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
    }

    private void init() {
        mRecyclerView = findViewById(R.id.recycler);
        mSongAdapter = ((App) getApplication()).getSongAdapter();
        mSongAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mSongAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadMusicFileList();
    }

    private void loadMusicFileList() {
        ContentResolver cr = getContentResolver();

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                mSongAdapter.addItem(new Song(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(5),
                        cursor.getString(3)
                ));
            }
        }
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            init();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.permDialogTitle)
                    .setMessage(R.string.requestPermissionMessage)
                    .setPositiveButton(R.string.OkLabel, (dialogInterface, i) ->
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE))
                    .create()
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0].equalsIgnoreCase(Manifest.permission.READ_EXTERNAL_STORAGE) &&
                    grantResults[0] == PERMISSION_GRANTED) {
                requestPermissions();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.permDialogTitle)
                        .setMessage(R.string.notGrantedPermMessage)
                        .setPositiveButton(R.string.OkLabel, (dialogInterface, i) -> finish())
                        .create()
                        .show();
            }
        }
    }

    @Override
    public void onItemClick(Song song) {
        SecondActivity.start(this, mSongAdapter.getIndex(song));
    }
}

