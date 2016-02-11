package edu.uw.motiondemo;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v4.view.MotionEventCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;
import java.util.List;

public class SaberActivity extends Activity implements SensorEventListener {

    private static final String TAG = "**SABER**";
    boolean soundsaved_saber = false;
    boolean soundsaved_saber1 = false;
    boolean soundsaved_saber2 = false;
    boolean soundsaved_saber3 = false;
    boolean soundsaved_saber4 = false;
    int saber_on;
    int saber_swing1;
    int saber_swing2;
    int saber_swing3;
    int saber_swing4;
    SoundPool soundpool;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    //TODO: Add instance variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saber);

        initializeSoundPool();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

//        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
//        for(Sensor sensor : sensors) {
//            Log.v(TAG, sensor + "");
//        }

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (mAccelerometer == null) {
            finish(); // Accelerometer not found on phone
        }
    }

    @Override
    protected void onResume() {
        //register sensor
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        super.onResume();
    }

    @Override
    protected void onPause() {
        //unregister sensor

        super.onPause();
    }

    //helper method for setting up the sound pool
    @SuppressWarnings("deprecation")
    private void initializeSoundPool() {
        //TODO: Create the SoundPool
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundpool = new SoundPool.Builder()
                    .setMaxStreams(3)
                    .setAudioAttributes(attributes)
                    .build();
        } else {
            //API < 21
            soundpool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);

        }


        soundpool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (status == 0 && sampleId == saber_on) {
                    soundsaved_saber = true;

                } else if (status == 0 && sampleId == saber_swing1) {
                    soundsaved_saber1 = true;

                } else if (status == 0 && sampleId == saber_swing2) {
                    soundsaved_saber2 = true;

                } else if (status == 0 && sampleId == saber_swing3) {
                    soundsaved_saber3 = true;

                } else if (status == 0 && sampleId == saber_swing4) {
                    soundsaved_saber4 = true;
                }
                if (status == 0 && soundsaved_saber && soundsaved_saber1 && soundsaved_saber2
                        && soundsaved_saber3 && soundsaved_saber4) {
                    playsound(saber_on, soundsaved_saber);
                }
            }
        });
        saber_on = soundpool.load(SaberActivity.this, R.raw.saber_on, 1);
        saber_swing1 = soundpool.load(SaberActivity.this, R.raw.saber_swing1, 1);
        saber_swing2 = soundpool.load(SaberActivity.this, R.raw.saber_swing2, 1);
        saber_swing3 = soundpool.load(SaberActivity.this, R.raw.saber_swing3, 1);
        saber_swing4 = soundpool.load(SaberActivity.this, R.raw.saber_swing4, 1);

        //TODO: Load the sounds
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                handleTap(event.getX(), event.getY());
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    //helper method for handling tap logic
    public void handleTap(double x, double y) {
        View view = findViewById(R.id.saberView);
        int width = view.getWidth();
        int height = view.getHeight();

        int quadrant;
        if (x > width / 2 && y < height / 2) {
            quadrant = 1;
            playsound(saber_swing1, soundsaved_saber1);
        } else if (x < width / 2 && y < height / 2) {
            quadrant = 2;
            playsound(saber_swing2, soundsaved_saber2);
        } else if (x < width / 2 && y > height / 2) {
            quadrant = 3;
            playsound(saber_swing3, soundsaved_saber3);
        } else {
            quadrant = 4;
            playsound(saber_swing4, soundsaved_saber4);
        }

        Log.v(TAG, "Tap in quadrant: " + quadrant);

        //TODO: Play sound depending on quadrant!

    }

    //for immersive full-screen (from API guide)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            final View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void playsound(int id, boolean soundsave) {
        if (soundsave) {
            soundpool.play(id, 1, 1, 1, 0, 1f);
        } else {
            Log.v(TAG, "soundsaved is not true; sound not loaded. ID: " + id);
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.v(TAG, Arrays.toString(event.values));

        if (Math.abs(event.values[0]) > 1.0) {
            Log.v(TAG, "shook left");
            playsound(4, soundsaved_saber4);

        }else if(Math.abs(event.values[1]) > 1.0){
            Log.v(TAG, "shook up");
            playsound(2, soundsaved_saber2);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
