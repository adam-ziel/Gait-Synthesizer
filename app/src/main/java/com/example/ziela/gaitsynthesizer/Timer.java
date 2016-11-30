package com.example.ziela.gaitsynthesizer;

/**
 * This class acts as a stopwatch, by polling system time on each step event.
 * It compares each pair of consecutive steps with each other, and progresses the user
 * through the musical sequence depending on how regular their ratio is.
 */
public class Timer
{
    private long startTime;

    private long[] pastTwoStepIntervals = {0, 0};

    private double tolerance = 0.1;

    private double deviation;

    private boolean TIMER_IDLE = true;


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
            compare(); // compare timer deviations against tolerance
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

        updateTimerDisplays();
    }


    /**
     * Gets the ratio between the current, and previous step intervals,
     * then compares it against a tolerance value.
     * If outside the tolerance, all times are cleared, and the stepcount is reset.
     */
    public void compare()
    {
        if (pastTwoStepIntervals[1] != 0) // implies there are two values in the buffer
        {
            deviation = (double) pastTwoStepIntervals[0] / pastTwoStepIntervals[1];

            MainActivity.setDeviationDisplay("Deviation: " +
                    String.format("%.1f", (deviation * 100) - 100) + "%");

            if (Math.abs(1 - deviation) > tolerance) // reset if outside the tolerance
            {
                resetStepCountAndTimerBuffer();
            }
        }
    }


    /**
     * Protocol for handling steps that fall outside of the regularity tolerance
     */
    public void resetStepCountAndTimerBuffer()
    {
        MainActivity.resetStepCount();

        resetTimerBuffer();

        updateTimerDisplays();

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


    /**
     * Updates the TextView instances in MainActivity
     */
    public void updateTimerDisplays()
    {
        MainActivity.setTimer1Display("Timer 1:" + pastTwoStepIntervals[0]);
        MainActivity.setTimer2Display("Timer 2: " + pastTwoStepIntervals[1]);
    }
}