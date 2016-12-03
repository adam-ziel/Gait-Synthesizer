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

    private static int stepCount = 0;

    private int lastStep;

    private boolean firstStep = true;

    private static TextView stepCountDisplay;

    private static TextView timer1Display;

    private static TextView timer2Display;

    private static TextView deviationDisplay;

    public static final int ROOT = 0;

    public static final int THIRD = 2;

    public static final int FIFTH = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //super scary stuff
        //uses java class background changer as its layout instead of an xml layout
        View view = new MainGUI(this);
        setContentView(view);
        //setContentView(R.layout.activity_main);

        int rootNote = InputActivity.getInputNote(); // starting note in scale

        scaleFrequencies = populateScale(rootNote, majorScaleSteps);

        createFrequencyBufferForEachScaleIndex();

        getXMLHandles();

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
            timer.onStep();

            advanceNoteSequence();
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
            timer.onStep();

            advanceNoteSequence();
        }

        return false;
    }


    /**
     * Stop previous note, play next one, and increment step count
     */
    public void advanceNoteSequence()
    {
//        if (stepCount > 8)
//            playChord();
//

        if (!firstStep)
            bufferPool[lastStep].stop();

        bufferPool[stepCount%8].play();
        lastStep = stepCount%8;

        stepCountDisplay.setText("Step Detector Detected: " + stepCount);
        stepCount++;

        firstStep = false;
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
        double[] scaleFrequencies = new double[8]; // wasteful? but i guess killed once we return?

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
        FrequencyBuffer note1 = new FrequencyBuffer(scaleFrequencies[0]);
        FrequencyBuffer note2 = new FrequencyBuffer(scaleFrequencies[1]);
        FrequencyBuffer note3 = new FrequencyBuffer(scaleFrequencies[2]);
        FrequencyBuffer note4 = new FrequencyBuffer(scaleFrequencies[3]);
        FrequencyBuffer note5 = new FrequencyBuffer(scaleFrequencies[4]);
        FrequencyBuffer note6 = new FrequencyBuffer(scaleFrequencies[5]);
        FrequencyBuffer note7 = new FrequencyBuffer(scaleFrequencies[6]);
        FrequencyBuffer note8 = new FrequencyBuffer(scaleFrequencies[7]);

        bufferPool[0] = note1;
        bufferPool[1] = note2;
        bufferPool[2] = note3;
        bufferPool[3] = note4;
        bufferPool[4] = note5;
        bufferPool[5] = note6;
        bufferPool[6] = note7;
        bufferPool[7] = note8;
    }


    /**
     * Retrieves handles to all XML elements we need to modify
     */
    public void getXMLHandles()
    {
        stepCountDisplay = (TextView) findViewById(R.id.mainSteps);
        timer1Display = (TextView) findViewById(R.id.timer1Text);
        timer2Display = (TextView) findViewById(R.id.timer2Text);
        deviationDisplay = (TextView) findViewById(R.id.deviationText);

        View v = findViewById(R.id.simulatorButton);

        if (v != null)
            v.setOnTouchListener(this);
    }


    /**
     * Matt, please rename. What is this doing?
     */
    public void prepareStepDetector()
    {
        SensorManager sensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);

        Sensor stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        //link up the sensor. I only want to do this once. I will always keep it linked
        sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }


    /**
     * David, please rename
     */
    public void configurePowerManager()
    {
        PowerManager mgr = (PowerManager)getSystemService(this.POWER_SERVICE);

        wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
    }


    /**
     * David's
     */
    protected void onResume()
    {
        super.onResume();

        if (wakeLock.isHeld())
            wakeLock.release(); //dont need to worry about keeping CPU on, the screen is back on
    }


    /**
     * David's
     */
    protected void onStop()
    {
        super.onStop();

        if (!wakeLock.isHeld())
            wakeLock.acquire(); //I want to keep going when the screen is off so keep CPU on
    }

    public static int getStepCount()
    {
        return stepCount;
    }

    public boolean getFirstStep()
    {
        return getFirstStep();
    }


    public static void setDeviationDisplay(String message) { deviationDisplay.setText(message); }

    public static void setTimer1Display(String message) { timer1Display.setText(message); }

    public static void setTimer2Display(String message) { timer2Display.setText(message); }

    public static void resetStepCount()
    {
        stepCount = 0;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}