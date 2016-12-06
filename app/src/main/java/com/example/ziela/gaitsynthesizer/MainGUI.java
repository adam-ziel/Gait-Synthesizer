package com.example.ziela.gaitsynthesizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import java.util.Locale;

public class MainGUI extends View {

    private Paint paint;
    private static final int radius = 120;
    private static final float START_X_POS = 700;
    private static final float START_Y_POS = 620;
    private static final float X_POS_OFFSET = 200;
    private static final float Y_POS_OFFSET = 200;
    private static float[] circleXPos = new float[8]; // X coordinates of circles
    private static float[] circleYPos = new float[8]; // Y coordinates of circles

    public MainGUI( Context context ) {
        super( context );
        initializeCircleCoordinates(); // populate coordinate arrays
        paint = new Paint();
        paint.setTextSize(80);
    }

    @Override
    /*
     * Draws GUI using canvas rather than XML files
     */
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        int localCounter;
        if(MainActivity.getFirstStep()){
            localCounter = MainActivity.getCurrentConsecutiveStepCount();
        }else{
            localCounter = MainActivity.getCurrentConsecutiveStepCount() - 1;
        }

        for (int i = 0; i < 8; i ++){
            paint.setColor(Color.GRAY); // Gray circles indicate non-active tones
            if (i != ((localCounter)%8)) {
                paint.setColor(Color.GRAY); // Reset all non-active tones to gray
                canvas.drawCircle(circleXPos[i], circleYPos[i], radius, paint);
            }else {
                paint.setColor(Color.GREEN); // Set active tone to green
                canvas.drawCircle(circleXPos[i], circleYPos[i], radius, paint);
                paint.setColor(Color.GRAY);
            }
        }
        canvas.drawText("Touch anywhere to play", 100, 100, paint);
        canvas.drawText(
                String.format( Locale.getDefault(),
                        "Total Steps Taken: %d",
                        MainActivity.getTotalStepCount()
                ),
                100, 225, paint
        );
        canvas.drawText(
                String.format( Locale.getDefault(),
                        "Total Consecutive Steps: %d",
                        (MainActivity.getTotalStepCount() - MainActivity.getTotalNonConsecutiveStepCount())
                ),
                100, 300, paint
        );
        canvas.drawText(
                String.format( Locale.getDefault(),
                        "Current Consecutive Steps: %d",
                        MainActivity.getCurrentConsecutiveStepCount()
                ),
                100, 375, paint
        );
        canvas.drawText(
                String.format( Locale.getDefault(),
                        "Maximum Consecutive Steps: %d",
                        MainActivity.getMaxConsecutiveStepCount()
                ),
                100, 450, paint
        );
        canvas.drawText(
                String.format( Locale.getDefault(),
                        "Timer 1: %dms",
                        (int) Timer.getTimer1()
                ),
                80, circleYPos[4] + 400, paint
        );
        canvas.drawText(
                String.format( Locale.getDefault(),
                        "Timer 2: %dms",
                        (int) Timer.getTimer2()
                ),
                860, circleYPos[4] + 400, paint
        );
        canvas.drawText(
                String.format( Locale.getDefault(),
                        "Most Recent Deviation: %d%%",
                        (int) (100 * Timer.getPercentDeviation())
                ),
                80, circleYPos[4] + 475, paint
        );
        canvas.drawText(
                String.format( Locale.getDefault(),
                        "Steps Outside %d%% Deviation: %d%%",
                        (int) (100 * Timer.getPercentTolerance()),
                        (int) (100 * MainActivity.getPercentConsecutiveSteps())
                ),
                80, circleYPos[4] + 550, paint
        );
        invalidate(); // redraw canvas
    }

    /**
     * Method that handles defining pixel coordinates on 8 circles
     *      0       0(Xstart, Ystart)
     *    7  1      7(Xstart - 1*Xoff, Ystart + 1*Yoff) 1(Xstart + 1*Xoff, Ystart + 1*Yoff)
     *   6    2     6(Xstart - 2*Xoff, Ystart + 2*Yoff) 2(Xstart + 1*Xoff, Ystart + 2*Yoff)
     *    5  3      5(Xstart - 1*Xoff, Ystart + 3*Yoff) 3(Xstart + 1*Xoff, Ystart + 3*Yoff)
     *      4       4(Xstart, Ystart + 4*Yoff)
     */
    private void initializeCircleCoordinates(){
        //x coordinates
        // loop 0,1,2 and 4,5,6 diagonals : x offset increments are proportional to circle number
        for( int i = 0; i < 3; i++ ){
            circleXPos[ i ] = START_X_POS + i * X_POS_OFFSET;
            circleXPos[ i+4 ] = START_X_POS - i * X_POS_OFFSET;
        }
        circleXPos[3] = START_X_POS + 1 * X_POS_OFFSET;
        circleXPos[7] = START_X_POS - 1 * X_POS_OFFSET;
        //y coordinates
        //loop 0,1,2,3 and 4,5,6,7 : y offset increments are proportional to circle number
        for( int j = 0; j < 4; j++ ) {
            circleYPos[ j ] = START_Y_POS + j * Y_POS_OFFSET;
            circleYPos[ j+4 ] = START_Y_POS - (j-4) * Y_POS_OFFSET;
        }
    }
}
