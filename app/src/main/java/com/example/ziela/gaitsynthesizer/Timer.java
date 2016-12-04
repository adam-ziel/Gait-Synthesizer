package com.example.ziela.gaitsynthesizer;

/**
 * This class acts as a stopwatch, by polling system time on each step event.
 * It compares each pair of consecutive steps with each other, and progresses the user
 * through the musical sequence depending on how regular their ratio is.
 */
public class Timer {
    private static double tolerance = 0.15;
    private static boolean TIMER_IDLE = true;
    private static long startTime;
    private static long[] stepIntervals = {0, 0};
    private static double deviation;

    public static void resetMetrics() { // TODO move out of class
        MainActivity.resetStepCount();
        resetTimer();
    }

    /**
     * Timer routine called every time a step is detected
     */
    public static void onStep() {
        if (TIMER_IDLE) // i.e. there's no active timer we have to stop
            start();
        else {
            stop();
            compareStepIntervals();
            start();
        }
    }

    /**
     * Records start time, and puts down TIMER_IDLE flag
     */
    public static void start() {
        startTime = System.currentTimeMillis();
        TIMER_IDLE = false;
    }

    /**
     * Right shifts array contents to make room for new time,
     * then places the new step interval in index zero
     */
    public static void stop() { // TODO use mod to eliminate double rewrites
        stepIntervals[1] = stepIntervals[0]; // shift right to vacate index 0
        stepIntervals[0] = System.currentTimeMillis() - startTime;
    }

    /**
     * Gets the ratio between the current, and previous step intervals,
     * then compares it against a tolerance value.
     * If outside the tolerance, all times are cleared, and the stepcount is reset.
     */
    public static void compareStepIntervals() {
        if ( (stepIntervals[0] != 0) && (stepIntervals[1] != 0) ){
            deviation = (double) stepIntervals[0] / stepIntervals[1];
            if ( !withinTolerance() )
                resetMetrics(); // TODO update to reflect moved method
        }
    }
    public static boolean withinTolerance() {
        return Math.abs(1 - deviation) <= tolerance;
    }
    public static void resetTimer() { // TODO should not be public?
        stepIntervals[0] = 0;
        stepIntervals[1] = 0;
        TIMER_IDLE = true;
    }
    public static double getTimer1() {
        return (double) stepIntervals[0];
    }
    public static double getTimer2() {
        return (double) stepIntervals[1];
    }
    public static double getDeviation() {
        return deviation;
    }
}