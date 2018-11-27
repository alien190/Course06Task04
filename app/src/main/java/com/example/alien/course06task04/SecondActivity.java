package com.example.alien.course06task04;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class SecondActivity extends AppCompatActivity {
    private static final String PATH_KEY = "SecondActivity.PathKey";
    private static final String ARTIST_KEY = "SecondActivity.ArtistKey";
    private static final String TITLE_KEY = "SecondActivity.TitleKey";
    private static final int SEEK_VALUE_MSEC = 2000;
    private MediaPlayer mMediaPlayer;
    private ImageButton mRewindImageButton;
    private ImageButton mPlayImageButton;
    private ImageButton mPauseImageButton;
    private ImageButton mStopImageButton;
    private ImageButton mForwardImageButton;
    private ProgressBar mProgressBar;
    private TextView mArtistTextView;
    private TextView mTitleTextView;
    private boolean mIsPaused = false;
    private Disposable mDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        initUI(getIntent().getStringExtra(ARTIST_KEY), getIntent().getStringExtra(TITLE_KEY));
        initPlayer(getIntent().getStringExtra(PATH_KEY));
    }

    private void initUI(String artist, String title) {
        mRewindImageButton = findViewById(R.id.ib_rewind);
        mPlayImageButton = findViewById(R.id.ib_play);
        mPauseImageButton = findViewById(R.id.ib_pause);
        mStopImageButton = findViewById(R.id.ib_stop);
        mForwardImageButton = findViewById(R.id.ib_forward);
        mProgressBar = findViewById(R.id.progress_bar);
        mTitleTextView = findViewById(R.id.tv_title);
        mArtistTextView = findViewById(R.id.tv_artist);
        mPauseImageButton.setEnabled(false);
        mStopImageButton.setEnabled(false);
        if (artist != null) {
            mArtistTextView.setText(artist);
        }
        if (title != null) {
            mTitleTextView.setText(title);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRewindImageButton.setOnClickListener(this::OnClick);
        mPlayImageButton.setOnClickListener(this::OnClick);
        mPauseImageButton.setOnClickListener(this::OnClick);
        mStopImageButton.setOnClickListener(this::OnClick);
        mForwardImageButton.setOnClickListener(this::OnClick);
    }

    @Override
    protected void onStop() {
        mRewindImageButton.setOnClickListener(null);
        mPlayImageButton.setOnClickListener(null);
        mPauseImageButton.setOnClickListener(null);
        mStopImageButton.setOnClickListener(null);
        mForwardImageButton.setOnClickListener(null);
        super.onStop();
    }

    private void initPlayer(String path) {
        if (path != null && !path.isEmpty()) {
            mMediaPlayer = new MediaPlayer();
            try {
                mMediaPlayer.setDataSource(path);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            play();
        }
    }

    @Override
    protected void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        super.onDestroy();
    }

    private void OnClick(View view) {
        if (view != null && mMediaPlayer != null) {
            switch (view.getId()) {
                case R.id.ib_play: {
                    play();
                    break;
                }
                case R.id.ib_stop: {
                    stop();
                    break;
                }
                case R.id.ib_pause: {
                    pause();
                    break;
                }
                case R.id.ib_forward: {
                    forward();
                    break;
                }
                case R.id.ib_rewind: {
                    rewind();
                    break;
                }
            }
        }
    }

    private void play() {
        if (mIsPaused) {
            mMediaPlayer.start();
            mIsPaused = false;
        } else {
            try {
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        mProgressBar.setMax(mMediaPlayer.getDuration());
        mPlayImageButton.setEnabled(false);
        mStopImageButton.setEnabled(true);
        mPauseImageButton.setEnabled(true);
        mRewindImageButton.setEnabled(true);
        mForwardImageButton.setEnabled(true);
        dispose();
        mDisposable = Flowable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> updatePosition(), Throwable::printStackTrace);
    }


    private void pause() {
        mMediaPlayer.pause();
        mIsPaused = true;
        mPauseImageButton.setEnabled(false);
        mPlayImageButton.setEnabled(true);
        mStopImageButton.setEnabled(true);
    }

    private void stop() {
        mMediaPlayer.stop();
        mIsPaused = false;
        mPauseImageButton.setEnabled(false);
        mStopImageButton.setEnabled(false);
        mPlayImageButton.setEnabled(true);
        mRewindImageButton.setEnabled(false);
        mForwardImageButton.setEnabled(false);
        mProgressBar.setProgress(0);
        dispose();
    }

    private void dispose() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    private void forward() {
        int pos = mMediaPlayer.getCurrentPosition();
        mMediaPlayer.seekTo(pos + SEEK_VALUE_MSEC);
        updatePosition();
    }

    private void rewind() {
        int pos = mMediaPlayer.getCurrentPosition();
        mMediaPlayer.seekTo(pos - SEEK_VALUE_MSEC);
        updatePosition();
    }

    private void updatePosition() {
        mProgressBar.setProgress(mMediaPlayer.getCurrentPosition());
    }

    public static void start(Context context, Song song) {
        Intent intent = new Intent(context, SecondActivity.class);
        intent.putExtra(PATH_KEY, song.getPath());
        intent.putExtra(ARTIST_KEY, song.getArtist());
        intent.putExtra(TITLE_KEY, song.getTitle());
        context.startActivity(intent);
    }

}
