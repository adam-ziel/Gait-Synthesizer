package com.example.ziela.gaitsynthesizer;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * This class will be used for setting up the pedometer.
 * There is a calibration phase for the accelerometers. Anywhere from 4-10 steps usually.
 * This means steps are not tracked until its calibrated, so I want to use this class to do the calibration
 * phase and THEN pass it to the music synthesis.
 */
public class ConfigActivity extends StepDetector {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView(R.layout.activity_configuration);

        // TODO gif without internet load
        WebView webView = (WebView) findViewById(R.id.web_view);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://thomas.vanhoutte.be/miniblog/wp-content/uploads/light_blue_material_design_loading.gif");
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        initializeStepListener();

        // get button handle and set listener
        View v = findViewById(R.id.bypassButton);
        if (v != null)
            v.setOnTouchListener(this);
    }

    @Override
    /*
     * This method detects buttons presses,
     * and bypasses the step detector calibration
     */
    public boolean onTouch( View v, MotionEvent event ) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Intent main = new Intent( ConfigActivity.this, MainActivity.class );
            ConfigActivity.this.startActivity( main );
            finish();
        }
        return false;
    }

    @Override
    /**
     * Called every time a sensor event occurs.
     * If the event is a STEP_DETECT, launch into MainActivity
     *
     * @param event           step detection event
     */
    public void onSensorChanged( SensorEvent event ) {
        Sensor sensor = event.sensor;
        // once we detect a step, calibration is finished, so move to main activity
        if( sensor.getType() == Sensor.TYPE_STEP_DETECTOR ) {
            Intent main = new Intent( ConfigActivity.this, MainActivity.class );
            ConfigActivity.this.startActivity( main );
            finish();
        }
    }
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener( this, stepDetectorSensor,
                                         SensorManager.SENSOR_DELAY_FASTEST );
    }
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener( this, stepDetectorSensor );
    }
}