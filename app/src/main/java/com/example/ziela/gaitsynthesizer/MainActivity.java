package com.example.ziela.gaitsynthesizer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.PowerManager;
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
    //private int[] minorScaleSteps = {0, 2, 3, 5, 7, 8, 10, 12}; not used

    private Timer timer = new Timer();

    private int lastStep;

    private static boolean firstStep = true;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //super scary stuff
        //uses java class background changer as its layout instead of an xml layout
        View view = new MainGUI(this);
        setContentView(view); //use my gui
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
     * @param event the accelerometer has registerd a step
     */
    public void onSensorChanged(SensorEvent event)
    {
        if ((!timer.percentDeviationIsOutsideTolerance())){
            incrementStepCounts(true); //his step was ok
        }else{
            //steps took too long reset us back
            timer.resetTimer();
            incrementStepCounts(false);
        }
        Timer.recordTimeInterval(); //set the time stamps
        //calculate the difference
        sumTimeDifferences = sumTimeDifferences + (int) Math.abs(Timer.getTimeIntervals()[0] - Timer.getTimeIntervals()[1]);
        advanceNoteSequence(); //advance the note
    }


    @Override
    /**
     * Simulated step detect event using button
     */
    public boolean onTouch(View v, MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            if ((!timer.percentDeviationIsOutsideTolerance())){
                incrementStepCounts(true); //his step was ok
            }else{
                //steps took too long reset us back
                timer.resetTimer();
                incrementStepCounts(false);
            }
            Timer.recordTimeInterval(); //set the time stamps
            //calculate the difference
            sumTimeDifferences = sumTimeDifferences + (int) Math.abs(Timer.getTimeIntervals()[0] - Timer.getTimeIntervals()[1]);
            advanceNoteSequence();//advance the note
        }
        return false;
    }


    /**
     * Stop previous note, play next one, and increment step count
     */
    public void advanceNoteSequence()
    {
        //prevent us from stopping a buffer that was not playing
        if (!firstStep) {
            bufferPool[lastStep].stop();
        }
        bufferPool[currentConsecutiveStepCount%8].play();
        lastStep = currentConsecutiveStepCount%8;

        firstStep = false; //we've taken a step
    }

    /**
     * Iterates through 8-entry frequency array, populating with
     * scale degrees based on scaleStep array
     */
    public static double[] populateScale(int rootNote, int[] scaleSteps)
    {
        double[] scaleFrequencies = new double[8]; // wasteful? but i guess killed once we return?

        //populating the scales. Math
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
            bufferPool[i] = new FrequencyBuffer(scaleFrequencies[i]);
        }
    }


    /**
     * Linking the accelerometer.
     */
    public void prepareStepDetector()
    {
        SensorManager sensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);

        Sensor stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        //link up the sensor. I only want to do this once. I will always keep it linked
        sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }


    /**
     * Telling the application to never stop running even if the user minimizes or turns off screen
     */
    public void configurePowerManager()
    {
        PowerManager mgr = (PowerManager)getSystemService(this.POWER_SERVICE);

        wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
    }


    /**
     * User has tabbed back into the application
     */
    protected void onResume()
    {
        super.onResume();

        if (wakeLock.isHeld())
            wakeLock.release(); //dont need to worry about keeping CPU on, the screen is back on
    }


    /**
     * User has left the application either by minimizing, tabbing out, or turning off screen
     */
    protected void onStop()
    {
        super.onStop();

        if (!wakeLock.isHeld()) {
            wakeLock.acquire(); //I want to keep going when the screen is off so keep CPU on
        }
    }

    // begin metrics
    private static int totalStepCount = 0;
    private static int currentConsecutiveStepCount = 0;
    private static int maxConsecutiveStepCount = 0;
    private static int totalNonConsecutiveStepCount = 0;
    private static int sumTimeDifferences = 0;

    /**
     * Increments all of the step related counter metrics
     * @param isConsecutiveStep valid user step
     */
    public static void incrementStepCounts(boolean isConsecutiveStep){
        totalStepCount++;
        if (isConsecutiveStep){
            currentConsecutiveStepCount++;
            if (currentConsecutiveStepCount > maxConsecutiveStepCount) {
                maxConsecutiveStepCount = currentConsecutiveStepCount;
            }
        }else{
            currentConsecutiveStepCount = 0;
            totalNonConsecutiveStepCount++;
        }
    }

    //Aaron Carpenter is the hero we need. @ProfA****** I need a lock of your hair for my shrine
    //                                                                  -Adam

    /**
     * Return the current step count
     * @return number of valid steps the user has currently taken
     */
    public static int getCurrentConsecutiveStepCount()
    {
        return currentConsecutiveStepCount;
    }

    /**
     * Get the value of the most good steps the user has taken
     * @return maximum number of good steps the user took
     */
    public static int getMaxConsecutiveStepCount()
    {
        return maxConsecutiveStepCount;
    }

    /**
     * Gets how many steps were miss timed
     * @return max number of steps the user took that were back
     */
    public static int getTotalNonConsecutiveStepCount()
    {
        return totalNonConsecutiveStepCount;
    }

    /**
     * Get the number of how many steps the user has taken with the application running
     * @return totalnumber of steps the user took since opening the application
     */
    public static int getTotalStepCount()
    {
        return totalStepCount;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}