package com.example.ziela.gaitsynthesizer;

import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;

/**
 * Main app activity.
 * This is where we do all our audio playing and step detecting
 */
public class MainActivity extends StepDetector {
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        View view = new MainGUI( this );
        setContentView( view );
        this.initializeStepListener();
        if( view != null )
            view.setOnTouchListener( this );

        int rootNote = InputActivity.getInputNote(); // starting note in scale
        scaleFrequencies = populateScale( rootNote, majorScaleSteps );
        createScaleFrequencies();
        configurePowerManager();
    }

    // TODO move step methods to new class
    private static int totalStepCount = 0; // every step, good, bad, etc
    private static int currentStepCount = 0; // current consecutive steps
    private static int maxStepCount = 0; // max consecutive steps
    private static int missedStepCount = 0; // steps that reset the counter
    private static boolean firstStep = true;
    private static int lastStep;
    public static void onStep() {
        Timer.onStep();
        advanceNoteSequence();
        currentStepCount++;
        if( currentStepCount > maxStepCount )
            maxStepCount = currentStepCount;
    }
    public static void resetCurrentStepCount() {
        missedStepCount++;
        currentStepCount = 0;
    }
    public static int getCurrentStepCount() {
        return currentStepCount;
    }/*
    public static int getMaxStepCount(){
        return maxStepCount;
    }
    public static int getMissedStepCount(){
        return missedStepCount;
    }
    public static int getTotalStepCount(){
        return totalStepCount;
    }
    public static int getPercentGood(){
        return 100 * (1 - ( missedStepCount/totalStepCount ));
    }*/

    // TODO move music/scale methods to separate class
    public static final int ROOT = 0;
    public static final int THIRD = 2;
    public static final int FIFTH = 4;
    private static FrequencyBuffer[] bufferPool = new FrequencyBuffer[8];
    private static double[] scaleFrequencies = new double[8];
    private static final int[] majorScaleSteps = { 0, 2, 4, 5, 7, 9, 11, 12 };
    private static final int[] minorScaleSteps = { 0, 2, 3, 5, 7, 8, 10, 12 };
    /**
     * Stop previous note and play next one
     */
    public static void advanceNoteSequence() {
//        if (stepCount > 8)
//            playChord();
        if (!firstStep)
            bufferPool[ lastStep ].stop();
        bufferPool[ currentStepCount % 8 ].play();
        lastStep = currentStepCount % 8; // TODO
        firstStep = false; //TODO is this stepCount == 0?
    }
    public void playChord() {
        bufferPool[ ROOT ].play();
        bufferPool[ THIRD ].play();
        bufferPool[ FIFTH ].play();
    }
    /**
     * Iterates through 8-entry frequency array, populating with
     * scale degrees based on scaleStep array
     */
    public static double[] populateScale( int rootNote, int[] scaleSteps ) {
        double[] scaleFrequencies = new double[ 8 ]; // wasteful? but i guess killed once we return?
        for( int i = 0; i < 8; i++ ) {
            scaleFrequencies[ i ] = midiToFrequency( rootNote + scaleSteps[ i ] );
        }
        return scaleFrequencies;
    }
    /**
     * Returns frequency from input integer MIDI note
     */
    public static double midiToFrequency( int midiNote ) {
        return Math.pow(2, (double) (midiNote - 69) / 12) * 440;
    }
    /**
     * Creates FrequencyBuffer objects for each frequency in scaleFrequencies[],
     * then fills bufferPool[] with these objects.
     */
    public void createScaleFrequencies() {
        for( int i = 0; i < 8; i++ ){
            bufferPool[ i ] = new FrequencyBuffer( scaleFrequencies[ i ] );
        }
    }


    //TODO make PowerManager methods separate class
    PowerManager.WakeLock wakeLock; // TODO private public?
    /**
     * David, please rename
     */
    public void configurePowerManager() {
        PowerManager mgr = (PowerManager) getSystemService( this.POWER_SERVICE );
        wakeLock = mgr.newWakeLock( PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock" );
    }
    /**
     * David's
     */
    protected void onResume() {
        super.onResume();
        if( wakeLock.isHeld() )
            wakeLock.release(); // No longer need to force program to run since screen is on
    }
    /**
     * David's
     */
    protected void onStop() {
        super.onStop();
        if( !wakeLock.isHeld() )
            wakeLock.acquire(); // Force program to run when the screen is off
    }

}