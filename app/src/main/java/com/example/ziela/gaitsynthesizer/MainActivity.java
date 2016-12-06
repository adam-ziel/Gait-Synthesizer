package com.example.ziela.gaitsynthesizer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * Main app activity.
 * This is where we do all our audio playing and step detecting
 */
public class MainActivity extends AppCompatActivity
{
    private static Timer timer = new Timer();

    private static FrequencyBuffer[] bufferPool = new FrequencyBuffer[8];
    private static double[] scaleFrequencies = new double[8];
    private static int[] majorScaleSteps = {0, 2, 4, 5, 7, 9, 11, 12};
    private static int[] minorScaleSteps = {0, 2, 3, 5, 7, 8, 10, 12};
    public static final int ROOT = 0;
    public static final int THIRD = 2;
    public static final int FIFTH = 4;

    private static boolean notePlaying = false;
    private static int indexPlaying = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View view = new MainGUI(this);
        StepHandler stepHandler = new StepHandler();
        setContentView(view);
        view.setOnTouchListener(stepHandler);

        int rootNote = InputActivity.getInputNote(); // starting note in scale
        scaleFrequencies = populateScale(rootNote, majorScaleSteps);
        createFrequencyBufferForEachScaleIndex();

        stepHandler.configureStepDetector(this);
        stepHandler.configurePowerManager(this);
    }

    public static void onStepSensorEvent()
    {
        Timer.recordTimeInterval();
        sumTimeDifferences = sumTimeDifferences + (int) Math.abs(Timer.getTimeIntervals()[0] - Timer.getTimeIntervals()[1]);
        advanceNoteSequence();
    }

    /**
     * Stop previous note, play next one, and increment step count
     */
    public static void advanceNoteSequence()
    {
        if (noteIsPlaying())
            bufferPool[indexPlaying].stop();
        bufferPool[currentConsecutiveStepCount % 8].play();
        indexPlaying = currentConsecutiveStepCount % 8;
        setIsNotePlaying(true);
    }

    // TODO create class / move to frequency buffer class?
    // begin music functions with no dependency
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

    public static boolean noteIsPlaying()
    {
        return notePlaying;
    }

    public static void setIsNotePlaying( boolean newNotePlaying )
    {
        notePlaying = newNotePlaying;
    }

    //end music functions with no dependency

    // TODO create class?
    // begin metrics
    private static int totalStepCount = 0;
    private static int currentConsecutiveStepCount = 0;
    private static int maxConsecutiveStepCount = 0;
    private static int totalNonConsecutiveStepCount = 0;
    private static int sumTimeDifferences = 0;

    public static void incrementStepCounts(boolean isConsecutiveStep){
        totalStepCount++;
        if (isConsecutiveStep){
            currentConsecutiveStepCount++;
            if (currentConsecutiveStepCount > maxConsecutiveStepCount)
                maxConsecutiveStepCount = currentConsecutiveStepCount;
        }else{
            currentConsecutiveStepCount = 0;
            totalNonConsecutiveStepCount++;
        }
    }

    public static void resetAllCounts(){
        totalStepCount = 0;
        currentConsecutiveStepCount = 0;
        maxConsecutiveStepCount = 0;
        totalNonConsecutiveStepCount = 0;
    }

    public static int getSumTimeDifferences() {
        return sumTimeDifferences;
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

    public static int getTotalStepCount()
    {
        return totalStepCount;
    }
    // end metrics

}