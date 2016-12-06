package com.example.ziela.gaitsynthesizer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.TextView;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.Locale;

/**
 * Main app activity.
 * This is where we do all our audio playing and step detecting
 */
public class MainActivity extends AppCompatActivity
        implements OnTouchListener, SensorEventListener
{
    PowerManager.WakeLock wakeLock;

    private FrequencyBuffer[] bufferPool = new FrequencyBuffer[8];
    private double[] scaleFrequencies = new double[8];
    private int[] majorScaleSteps = {0, 2, 4, 5, 7, 9, 11, 12};
    private int[] minorScaleSteps = {0, 2, 3, 5, 7, 8, 10, 12};

    private Timer timer = new Timer();
    private static boolean firstStep = true;
    private int lastStep;


    private static int totalStepCount = 0;
    private static int currentConsecutiveStepCount = 0;
    private static int maxConsecutiveStepCount = 0;
    private static int totalNonConsecutiveStepCount = 0;
    private static double percentConsecutiveSteps = 0;

    public static final int ROOT = 0;
    public static final int THIRD = 2;
    public static final int FIFTH = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View view = new MainGUI(this);
        setContentView(view);
        view.setOnTouchListener(this);

        int rootNote = InputActivity.getInputNote(); // starting note in scale
        scaleFrequencies = populateScale(rootNote, majorScaleSteps);
        createFrequencyBufferForEachScaleIndex();

        prepareStepDetector();

        configurePowerManager();
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
            onStepSensorEvent();
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
            onStepSensorEvent();
        }
        return false;
    }

    public void onStepSensorEvent()
    {
        timer.onStep();
        advanceNoteSequence();
        addTotalStepCount();
        if (currentConsecutiveStepCount > maxConsecutiveStepCount)
            maxConsecutiveStepCount = currentConsecutiveStepCount;
    }

    /**
     * Stop previous note, play next one, and increment step count
     */
    public void advanceNoteSequence()
    {
        if (!firstStep)
            bufferPool[lastStep].stop();

        bufferPool[currentConsecutiveStepCount %8].play();
        lastStep = currentConsecutiveStepCount %8;

        firstStep = false;
        currentConsecutiveStepCount++;
    }

    public void playChord()
    {
        bufferPool[ROOT].play();
        bufferPool[THIRD].play();
        bufferPool[FIFTH].play();
    }

    /**
     * Iterates through 8-entry frequency array, populating with
     * scale degrees based on scaleStep array
     */
    public static double[] populateScale(int rootNote, int[] scaleSteps)
    {
        double[] scaleFrequencies = new double[8];
        for (int i = 0; i < 8; i++)
        {
            scaleFrequencies[i] = midiNoteToFrequency(rootNote + scaleSteps[i]);
        }
        return scaleFrequencies;
    }

    /**
     * Returns frequency from input integer MIDI note
     */
    public static double midiNoteToFrequency(int midiNote)
    {
        return Math.pow(2, (double) (midiNote - 69) / 12) * 440;
    }

    /**
     * Creates FrequencyBuffer objects for each frequency in scaleFrequencies[],
     * then fills bufferPool[] with these objects.
     */
    public void createFrequencyBufferForEachScaleIndex()
    {
        for (int i = 0; i < 8; i++)
        {
            bufferPool[ i ] = new FrequencyBuffer(scaleFrequencies[ i ]);
        }
    }

    /**
     * TODO Matt, please rename. What is this doing?
     */
    public void prepareStepDetector()
    {
        SensorManager sensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
        Sensor stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        //link up the sensor. always keep it linked
        sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    /**
     * TODO David, please rename
     */
    public void configurePowerManager()
    {
        PowerManager mgr = (PowerManager)getSystemService(this.POWER_SERVICE);
        wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
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
    // end David's

    // TODO is this needed with step counts?
    public static boolean getFirstStep()
    {
        return firstStep;
    }

    // start metrics get set
    public static int getTotalStepCount()
    {
        return totalStepCount;
    }

    public static int getCurrentConsecutiveStepCount()
    {
        return currentConsecutiveStepCount;
    }

    public static int getMaxConsecutiveStepCount()
    {
        return maxConsecutiveStepCount;
    }

    public static int getTotalNonConsecutiveStepCount()
    {
        return totalNonConsecutiveStepCount;
    }

    public static void addNonConsecutiveStep()
    {
        totalNonConsecutiveStepCount++;
        updatePercentConsecutiveSteps();
    }

    public static void addTotalStepCount()
    {
        totalStepCount++;
        updatePercentConsecutiveSteps();
    }

    //TODO broken
    public static void updatePercentConsecutiveSteps()
    {
        if (totalStepCount != 0) {
            percentConsecutiveSteps = ( (totalNonConsecutiveStepCount/totalStepCount) );
        }else {
            percentConsecutiveSteps = 1;
        }
    }

    public static double getPercentConsecutiveSteps()
    {
        return percentConsecutiveSteps;
    }

    public static void resetCurrentCount()
    {
        currentConsecutiveStepCount = 0;
    }
    // end metrics

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
    }

}