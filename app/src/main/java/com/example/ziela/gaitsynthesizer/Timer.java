package com.example.ziela.gaitsynthesizer;

/**
 * Timer that records the two time intervals between the three most recent
 * recordTime calls and provides comparisons of these time intervals to each
 * other and a tolerance value.
 */
public class Timer
{
    private static long startTime;
    private static long[] timeIntervals = {0, 0};

    private static final double PERCENT_TOLERANCE = 0.15; //our maximum allowed tollerance
    private static double percentDeviation = 0;
    private static boolean TIMER_IDLE = true;

    /**
     * Updates percentDeviation to be the ratio between the standardDeviation
     * and average of the two stored time intervals.
     * If this ratio is outside the PERCENT_TOLERANCE, the timer and the
     * currentConsecutiveStepCount are reset.
     */
    public static void compareTimeIntervals()
    {
        if (!bufferIsEmpty())
        {
            double average = (double) (timeIntervals[0] + timeIntervals[1])/2;
            double standardDeviation = Math.sqrt(
                    Math.pow(timeIntervals[0] - average, 2) +
                            Math.pow(timeIntervals[1] - average, 2)
            );
            percentDeviation = standardDeviation / average;
        }
    }

    /**
     * Begins recording time interval if the timer is idle.
     * Stops recording time interval if the timer is running, compares the
     * stored time intervals, and then begins recording a new time interval.
     */
    public static void recordTimeInterval()
    {
        if (timerIsIdle()) {
            // i.e. there's no active timer we have to stop
            start();
        } else {
            stop();
            compareTimeIntervals();
            start();
        }
    }

    /**
     * Records start time, and puts down TIMER_IDLE flag
     */
    public static void start()
    {
        startTime = System.currentTimeMillis();
        TIMER_IDLE = false;
    }

    /**
     * Right shifts array contents to make room for new time,
     * then places the new time interval in index zero
     */
    public static void stop()
    {
        timeIntervals[1] = timeIntervals[0]; // shift right to vacate index 0
        timeIntervals[0] = System.currentTimeMillis() - startTime;
    }

    /**
     *
     * @return gets the current state of the timer
     */
    public static boolean timerIsIdle(){
        return TIMER_IDLE;
    }

    /**
     *
     * @return are we beyond the 10% deviation allowance
     */
    public static boolean percentDeviationIsOutsideTolerance()
    {
        return percentDeviation > PERCENT_TOLERANCE;
    }

    public static boolean bufferIsEmpty()
    {
        return ((timeIntervals[1] == 0)); //|| (timeIntervals[0] == 0));
    }

    /**
     * Sets both indices back to 0 and sets the timer idle flag
     */
    public static void resetTimer()
    {
        timeIntervals[0] = 0;
        timeIntervals[1] = 0;
        percentDeviation = 0;
        TIMER_IDLE = true;
    }

    /**
     *
     * @return both of the time intervals. Will be drawn to the screen
     */
    public static double[] getTimeIntervals()
    {
        return new double[]{(double) timeIntervals[0], (double) timeIntervals[1]};
    }

    public static double getPercentDeviation()
    {
        return percentDeviation;
    }

}