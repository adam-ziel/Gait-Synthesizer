package com.example.ziela.gaitsynthesizer;

/**
 * Created by ziela on 11/26/16.
 */

public class Timer
{
    private long startTime;

    private long stopTime;

    private long[] timerBuffer = {0, 0};

    double tolerance = 0.1;

    double deviation;

    boolean TIMER_IDLE = true;

    /**
     * Will be called every time a step is detected
     */
    public void listener()
    {
        if (TIMER_IDLE) // i.e. there's no active timer we have to stop
            start();
        else
        {
            stop(); // stop current timer
            compare(); // compare buffer values against tolerance
            start(); // start new timer
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

    public void stop()
    {
        timerBuffer[1] = timerBuffer[0]; // shift right so index 1 is empty

        timerBuffer[0] = System.currentTimeMillis() - startTime;

        MainActivity.timer1Display.setText("Timer 1:" + timerBuffer[0]);
        MainActivity.timer2Display.setText("Timer 2: " + timerBuffer[1]);
    }

    public void compare()
    {
        if (timerBuffer[1] != 0 ) // not ideal... determining if null would be better
        {
            deviation = (double) timerBuffer[1] / timerBuffer[0];
            MainActivity.deviationDisplay.setText("Deviation: " + String.format("%.3f", deviation));

            // reset if outside the tolerance
            if (Math.abs(1 - deviation) > tolerance)
            {
                timerBuffer[0] = 0;
                timerBuffer[1] = 0; // We should only have to reset this one

                MainActivity.timer1Display.setText("Timer 1:" + timerBuffer[0]);
                MainActivity.timer2Display.setText("Timer 2: " + timerBuffer[1]);

                TIMER_IDLE = true;

                MainActivity.resetStepCount();

                /* Reset frequency pool (return value??)*/
            }
        }

//        MainActivity.timer1Display.setText("Timer 1: " + timerBuffer[0]);

    }

    public long getTime1()
    {
        return timerBuffer[0];
    }

    public long getTime2()
    {
        return timerBuffer[1];
    }
}
