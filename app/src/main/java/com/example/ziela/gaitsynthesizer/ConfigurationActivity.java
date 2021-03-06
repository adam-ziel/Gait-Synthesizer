package com.example.ziela.gaitsynthesizer;


import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.view.View.OnTouchListener;

/**
 * This class will be used for setting up the pedometer.
 * There is a calibration phase for the accelerometers. Anywhere from 4-10 steps usually.
 * This means steps are not tracked until its calibrated, so I want to use this class to do the calibration
 * phase and THEN pass it to the music synthesis.
 */
public class ConfigurationActivity extends AppCompatActivity
        implements SensorEventListener, OnTouchListener
{
    private TextView textView, noteTextView;

    private SensorManager mSensorManager;
    private Sensor mStepDetectorSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);


        // get XML handles
        noteTextView = (TextView) findViewById(R.id.inputNoteTextView);
        textView = (TextView) findViewById(R.id.stepsTakenText);

        // again, what are these doing?
        mSensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
        mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        // get button handle and set listener
        View v = findViewById(R.id.bypassButton);
        if (v != null)
            v.setOnTouchListener(this);
    }

    /**
     * Called every time a sensor event occurs.
     * If the event is a STEP_DETECT, launch into MainActivity
     *
     * @param event the sensor has become calibrated
     */
    public void onSensorChanged(SensorEvent event)
    {
        Sensor sensor = event.sensor;

        // once we detect step, calibration is finished, so move to main activity
        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR)
        {
            Intent main = new Intent(ConfigurationActivity.this, MainActivity.class);

            ConfigurationActivity.this.startActivity(main);

            finish();
        }
    }

    @Override
    /**
     * This method detects buttons presses,
     * and bypasses the step detector calibration
     * @param v current context
     * @param event the user has touched the button
     */
    public boolean onTouch(View v, MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            Intent main = new Intent(ConfigurationActivity.this, MainActivity.class);

            ConfigurationActivity.this.startActivity(main);

            finish();
        }

        return false;
    }

    public void onAccuracyChanged(final Sensor sensor, int accuracy){
        // shouldn't be called, need to implement SensorEventListener
    }

    /**
     * User has reopened the application
     */
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    /**
     * User has minimized the application
     */
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this, mStepDetectorSensor);
    }
}