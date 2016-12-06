package com.example.ziela.gaitsynthesizer;

/**
 * This class acts as a stopwatch, by polling system time on each record event.
 * It compares each pair of consecutive record times with each other, and progresses the user
 * through the musical sequence depending on how regular their ratio is.
 */
public class Timer
{
    private long startTime;
    private static long[] timeIntervals = {0, 0};

    private static double percentTolerance = 0.2;
    private static double percentDeviation = 0;
    private static boolean TIMER_IDLE = true;

    /**
     * Timer routine called every time a step is detected
     */
    public void onStep()
    {
        if (timerIsIdle()) // i.e. there's no active timer we have to stop
            start();
        else
        {
            stop();
            compareTimeIntervals();
            start();
        }
    }

    /**
     * Records start time, and puts down TIMER_IDLE flag
     */
    public void start()
    {
        startTime = System.currentTimeMillis();
        TIMER_IDLE = false;
    }

    /**
     * Right shifts array contents to make room for new time,
     * then places the new step interval in index zero
     */
    public void stop()
    {
        timeIntervals[1] = timeIntervals[0]; // shift right to vacate index 0
        timeIntervals[0] = System.currentTimeMillis() - startTime;
    }

    /**
     * Gets the ratio between the current, and previous intervals,
     * then compares it against a percentTolerance value.
     * If outside the percentTolerance, all times are cleared, and the currentConsecutiveStepCount
     * is reset.
     */
    public void compareTimeIntervals()
    {
        if (!bufferIsEmpty())
        {
            double average = (double) (timeIntervals[0] + timeIntervals[1])/2;
            double standardDeviation = Math.sqrt( Math.pow(timeIntervals[0] - average, 2) +
                                   Math.pow(timeIntervals[1] - average, 2)
            );
            percentDeviation = standardDeviation / average;
            if (percentDeviationIsOutsideTolerance())
            {
                MainActivity.addNonConsecutiveStep();
                MainActivity.resetCurrentCount();
                resetTimer();
            }
        }
    }

    public boolean timerIsIdle(){
        return TIMER_IDLE;
    }

    public boolean percentDeviationIsOutsideTolerance()
    {
        return percentDeviation > percentTolerance;
    }

    public boolean bufferIsEmpty()
    {
        return ((timeIntervals[0] == 0) && (timeIntervals[1] == 0));
    }

    /**
     * Sets both indices back to 0 and sets the timer idle flag
     */
    public void resetTimer()
    {
        timeIntervals[0] = 0;
        timeIntervals[1] = 0;
        TIMER_IDLE = true;
    }

    public static double getTimer1()
    {
        return (double) timeIntervals[0];
    }

    public static double getTimer2()
    {
        return (double) timeIntervals[1];
    }

    public static double getPercentTolerance()
    {
        return percentTolerance;
    }

    public static double getPercentDeviation()
    {
        return percentDeviation;
    }

}