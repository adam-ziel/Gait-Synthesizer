package com.example.ziela.gaitsynthesizer;

/**
 * Created by ziela on 11/20/16.
 */

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

/**
 * This class will be used for setting up the pedometer.
 * There is a calibration phase for the accelerometers. Anywhere from 4-10 steps usually.
 * This means steps are not tracked until its calibrated, so I want to use this class to do the calibration
 * phase and THEN pass it to the music synthesis.
 */
public class ConfigurationActivity extends AppCompatActivity implements SensorEventListener {

    WebView webView;
    private TextView textView;
    private SensorManager mSensorManager;
    private Sensor mStepDetectorSensor;
    private int count = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        WebView webView = (WebView)
                findViewById(R.id.web_view);
        webView.setWebViewClient(new WebViewClient());//stay in app
        webView.loadUrl("https://thomas.vanhoutte.be/miniblog/wp-content/uploads/light_blue_material_design_loading.gif");
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);


        textView = (TextView) findViewById(R.id.stepsTakenText);

        mSensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
        mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

    }


    public void onAccuracyChanged(final Sensor sensor, int accuracy){
        // shouldn't be called, need to implement SensorEventListener
    }
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float[] values = event.values;
        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }

        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            // For test only. Only allowed value is 1.0 i.e. for step taken
            this.count++;
            textView.setText("Step Detector Detected : " + count);
            Intent synthesis = new Intent(ConfigurationActivity.this, MainActivity.class);
            ConfigurationActivity.this.startActivity(synthesis);
        }
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this, mStepDetectorSensor);
    }
}
