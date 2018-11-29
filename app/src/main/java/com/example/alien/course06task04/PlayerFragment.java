package com.example.alien.course06task04;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class PlayerFragment extends Fragment {

    private static final int SEEK_VALUE_MSEC = 2000;
    private static final int SHAKE_TRESHOLD = 600;
    private static final String INDEX_KEY = "PlayerFragment.IndexKey";
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
    private SensorManager mSensorManager;
    private Sensor mSensorAcceleration;
    private Sensor mSensorMagnetic;
    private long mLastUpdate;
    private float mLastX;
    private float mLastY;
    private float mLastZ;
    private SongAdapter mSongAdapter;
    private Song mSong;
    private View mView;
    final float[] mAccelReading = new float[3];
    final float[] mMagnetReading = new float[3];

    final float[] mRotationMatrix = new float[9];
    final float[] mOrientationAngles = new float[3];

    public static PlayerFragment newInstance(int index) {
        Bundle args = new Bundle();
        args.putInt(INDEX_KEY, index);
        PlayerFragment fragment = new PlayerFragment();
        fragment.setArguments(args);
        fragment.setRetainInstance(true);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fr_player, container, false);
            mSongAdapter = ((App) getActivity().getApplication()).getSongAdapter();
            mMediaPlayer = new MediaPlayer();
            Bundle args = getArguments();
            int index = 0;
            if (args != null) {
                index = args.getInt(INDEX_KEY);
            }
            initUI();
            initSong(index);
            showSong();
            initPlayer();
            initSensor();
        }
        return mView;
    }

    private void initSong(int index) {
        if (mSongAdapter != null) {
            mSong = mSongAdapter.getItem(index);
        }
    }

    private void initSensor() {
        try {
            mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            mSensorAcceleration =
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        } catch (Throwable throwable) {
            throwable.printStackTrace();

        }
    }

    private void initUI() {
        if (mView != null) {
            mRewindImageButton = mView.findViewById(R.id.ib_rewind);
            mPlayImageButton = mView.findViewById(R.id.ib_play);
            mPauseImageButton = mView.findViewById(R.id.ib_pause);
            mStopImageButton = mView.findViewById(R.id.ib_stop);
            mForwardImageButton = mView.findViewById(R.id.ib_forward);
            mProgressBar = mView.findViewById(R.id.progress_bar);
            mTitleTextView = mView.findViewById(R.id.tv_title);
            mArtistTextView = mView.findViewById(R.id.tv_artist);
            mPauseImageButton.setEnabled(false);
            mStopImageButton.setEnabled(false);
        }
    }

    private void showSong() {
        mArtistTextView.setText(mSong.getArtist());
        mTitleTextView.setText(mSong.getTitle());
    }

    @Override
    public void onStop() {
        super.onStop();
        mRewindImageButton.setOnClickListener(null);
        mPlayImageButton.setOnClickListener(null);
        mPauseImageButton.setOnClickListener(null);
        mStopImageButton.setOnClickListener(null);
        mForwardImageButton.setOnClickListener(null);
        mSensorManager.unregisterListener(mSensorListener);

    }

    @Override
    public void onStart() {
        super.onStart();
        mRewindImageButton.setOnClickListener(this::OnClick);
        mPlayImageButton.setOnClickListener(this::OnClick);
        mPauseImageButton.setOnClickListener(this::OnClick);
        mStopImageButton.setOnClickListener(this::OnClick);
        mForwardImageButton.setOnClickListener(this::OnClick);
        mSensorManager.registerListener(mSensorListener, mSensorAcceleration, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mSensorListener, mSensorMagnetic, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER: {
                    accelerometerChanged(event);
                    calculateOrientation();
                    break;
                }
                case Sensor.TYPE_MAGNETIC_FIELD: {
                    magneticChanged(event);
                    calculateOrientation();
                    break;
                }
                default: {
                    break;
                }
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private void calculateOrientation() {
        SensorManager.getRotationMatrix(mRotationMatrix, null, mAccelReading, mMagnetReading);
        SensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
        float angleX = (float) (Math.abs(mOrientationAngles[1]) * 180 / 3.1415);
        float angleY = (float) (Math.abs(mOrientationAngles[2]) * 180 / 3.1415);

        if (angleX < 15) {
            if (angleY < 15 && mIsPaused) {
                play();
            } else if (angleY > 165 && mMediaPlayer.isPlaying()) {
                pause();
            }
        }

    }

    private void magneticChanged(SensorEvent event) {
        System.arraycopy(event.values, 0, mMagnetReading, 0, mMagnetReading.length);
    }

    private void accelerometerChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        long currentTime = System.currentTimeMillis();
        long dt = currentTime - mLastUpdate;
        if (dt > 100) {
            mLastUpdate = currentTime;

            float speed = Math.abs(x + y + z - mLastX - mLastY - mLastZ) / dt * 10000;
            if (speed > SHAKE_TRESHOLD) {
                Toast.makeText(getActivity(), "Shaking!", Toast.LENGTH_SHORT).show();
                playRandomSong();
            }
            mLastX = x;
            mLastY = y;
            mLastZ = z;
        }

        System.arraycopy(event.values, 0, mAccelReading, 0, mAccelReading.length);
    }

    private void playRandomSong() {
        try {
            if (mMediaPlayer.isPlaying()) {
                int index = (int) ((mSongAdapter.getItemCount() - 1) * Math.random());
                stop();
                mMediaPlayer.reset();
                initSong(index);
                showSong();
                initPlayer();
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void initPlayer() {
        String path = mSong.getPath();
        if (path != null && !path.isEmpty()) {
            try {
                mMediaPlayer.setDataSource(path);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            play();
        }
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


}
