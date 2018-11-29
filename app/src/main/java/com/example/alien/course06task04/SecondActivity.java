package com.example.alien.course06task04;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class SecondActivity extends SingleFragmentActivity {
    private static final String INDEX_KEY = "SecondActivity.IndexKey";

    @Override
    protected Fragment getFragment() {
        int index = getIntent().getIntExtra(INDEX_KEY, 0);
        return PlayerFragment.newInstance(index);
    }

    public static void start(Context context, int index) {
        Intent intent = new Intent(context, SecondActivity.class);
        intent.putExtra(INDEX_KEY, index);
        context.startActivity(intent);
    }
}
