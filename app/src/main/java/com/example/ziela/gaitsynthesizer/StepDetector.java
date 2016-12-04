package com.example.ziela.gaitsynthesizer;

import android.support.v7.app.AppCompatActivity;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.View;
import android.view.MotionEvent;

public class StepDetector extends AppCompatActivity implements SensorEventListener, View.OnTouchListener {
    // SensorManager object provide access to sensors on a phone, stores values related to
    // sensor readings and provides means of disabling or destructing Sensor objects
    SensorManager sensorManager;
    // Sensor object provides access to information specific to the step detector sensor, most
    // importantly for our uses, the onchange event listener
    Sensor stepDetectorSensor;

    @Override
    /**
     * On screen touch, alert MainActivity that a step has been detected with onStep call.
     */
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            MainActivity.onStep();
            return true;
        }
        return false;
    }

    /**
     * On step detection by phones sensor, alert MainActivity that a step has been detected with
     * onStep call.
     *
     * @param event      step detector change event from registered listener
     */
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            MainActivity.onStep();
        }
    }

    /**
     * Initialize a listener for the step detector sensor by accessing the sensor manager and using
     * it to register an event listener for the phones default step detector at the sensors fastest
     * sampling speed.
     */
    public void initializeStepListener() {
        sensorManager = (SensorManager) getSystemService( SENSOR_SERVICE );
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR );
        sensorManager.registerListener( this, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST );
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Required method to extend sensor event listener
    }

}
