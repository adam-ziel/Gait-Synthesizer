package com.example.ziela.gaitsynthesizer;

import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.View;

import android.content.Context;

public class StepHandler extends AppCompatActivity
        implements View.OnTouchListener, SensorEventListener
{
    PowerManager.WakeLock wakeLock;

    public void configurePowerManager(Context context)
    {
        PowerManager mgr = (PowerManager) context.getSystemService(context.POWER_SERVICE);
        wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
    }
    /**
     * TODO Matt, please rename. What is this doing?
     */
    public void configureStepDetector(Context context)
    {
        SensorManager sensorManager =
                (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
        Sensor stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        //link up the sensor. always keep it linked
        sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    /**
     * Triggers timer, and advances note sequence on step detection event
     *
     * @param event
     */
    public void onSensorChanged(SensorEvent event)
    {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR)
        {
            MainActivity.onStepSensorEvent();
        }
    }

    @Override
    /**
     * Simulated step detect event using button
     */
    public boolean onTouch(View v, MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            MainActivity.onStepSensorEvent();
        }
        return false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
    }

    protected void onResume()
    {
        super.onResume();
        if (wakeLock.isHeld())
            wakeLock.release(); //dont need to worry about keeping CPU on, the screen is back on
    }

    protected void onStop()
    {
        super.onStop();
        if (!wakeLock.isHeld())
            wakeLock.acquire(); //I want to keep going when the screen is off so keep CPU on
    }
}
