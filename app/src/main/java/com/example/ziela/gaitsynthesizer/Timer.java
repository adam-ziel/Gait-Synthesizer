package com.example.ziela.gaitsynthesizer;

/**
 * This class acts as a stopwatch, by polling system time on each step event.
 * It compares each pair of consecutive steps with each other, and progresses the user
 * through the musical sequence depending on how regular their ratio is.
 */
public class Timer
{
    private long startTime;
    private static long[] pastTwoStepIntervals = {0, 0};

    private static double tolerance = 0.15;
    private static double deviationPercent = 0;
    private static boolean TIMER_IDLE = true;

    /**
     * Timer routine called every time a step is detected
     */
    public void onStep()
    {
        if (TIMER_IDLE) // i.e. there's no active timer we have to stop
            start();
        else
        {
            stop();
            comparePastTwoStepIntervals();
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
        pastTwoStepIntervals[1] = pastTwoStepIntervals[0]; // shift right to vacate index 0
        pastTwoStepIntervals[0] = System.currentTimeMillis() - startTime;
    }

    /**
     * Gets the ratio between the current, and previous step intervals,
     * then compares it against a tolerance value.
     * If outside the tolerance, all times are cleared, and the stepcount is reset.
     */
    public void comparePastTwoStepIntervals()
    {
        double average;
        double standardDeviation;
        if (bufferHasTwoValues())
        {
            average = (double) (pastTwoStepIntervals[0] + pastTwoStepIntervals[1])/2;
            standardDeviation = Math.sqrt( Math.pow(pastTwoStepIntervals[0] - average, 2) +
                                   Math.pow(pastTwoStepIntervals[1] -average, 2)
            );
            deviationPercent = standardDeviation / average;
            if (deviationPercentIsOutsideTolerance())
            {
                MainActivity.addNonConsecutiveStep();
                resetStepCountAndTimerBuffer();
            }
        }
    }


     public boolean deviationPercentIsOutsideTolerance()
    {
        return deviationPercent > tolerance;
    }


    public boolean bufferHasTwoValues()
    {
        return ((pastTwoStepIntervals[0] != 0) && (pastTwoStepIntervals[1] != 0));
    }

    /**
     * Protocol for handling steps that fall outside of the regularity tolerance
     */
    public void resetStepCountAndTimerBuffer()
    {
        MainActivity.resetCurrentCount();

        resetTimerBuffer();
        TIMER_IDLE = true;
    }

    /**
     * Sets both indices back to 0
     */
    public void resetTimerBuffer()
    {
        pastTwoStepIntervals[0] = 0;
        pastTwoStepIntervals[1] = 0;
    }

    public static double getTimer1()
    {
        return (double) pastTwoStepIntervals[0];
    }

    public static double getTimer2()
    {
        return (double) pastTwoStepIntervals[1];
    }

    public static double getDeviationPercent()
    {
        return deviationPercent;
    }

}