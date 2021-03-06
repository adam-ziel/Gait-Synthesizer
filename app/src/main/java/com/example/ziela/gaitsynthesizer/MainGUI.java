package com.example.ziela.gaitsynthesizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import java.util.Locale;

public class MainGUI extends View {

    private Paint paint;
    private static int textSize = 80;
    private static int verticalTextOffset = textSize + 40;
    private static int verticalAfterImageOffset = (textSize * 4) + 40;
    private static final int radius = (int) (textSize * 1.75);
    private static float START_X_POS = 720;
    private static float START_Y_POS = 1120 - (3 * radius);//(textSize * 9) + 60;
    private static float X_POS_OFFSET = textSize + radius; //200
    private static float Y_POS_OFFSET = textSize + radius; //200
    private static float[] circleXPos = new float[8]; // X coordinates of circles
    private static float[] circleYPos = new float[8]; // Y coordinates of circles

    // made for 1440 x 2240 phone
    public MainGUI( Context context ) {
        super( context );

        initializeCircleCoordinates(); // populate coordinate arrays

        paint = new Paint();
        paint.setTextSize(textSize);
    }

    @Override
    /**
     * Draws GUI using canvas rather than XML files
     */
    protected void onDraw(Canvas canvas)
    {
        int horizontalCenterPos = (canvas.getWidth() / 2); //center of the canvas

        canvas.drawColor(Color.WHITE);
        paint.setFakeBoldText(false);
        paint.setTextAlign(Paint.Align.CENTER);

        int localCounter = MainActivity.getCurrentConsecutiveStepCount();
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
        //plain text to the user. No changes
        canvas.drawText("Touch anywhere to simulate step",
                horizontalCenterPos, verticalTextOffset, paint
        );

        //draw the current number of consecutive time steps
        paint.setFakeBoldText(true);
        canvas.drawText(
                String.format( Locale.getDefault(),
                        "Current Consecutive Steps: %d",
                        MainActivity.getCurrentConsecutiveStepCount()
                ),
                horizontalCenterPos, verticalTextOffset + (3*textSize), paint
        );

        //draws the maximum number of consecutive steps
        canvas.drawText(
                String.format( Locale.getDefault(),
                        "Maximum Consecutive Steps: %d",
                        MainActivity.getMaxConsecutiveStepCount()
                ),
                horizontalCenterPos, verticalTextOffset + (4*textSize), paint
        );

        //gets the most recent step deviation
        canvas.drawText(
                String.format( Locale.getDefault(),
                        "Most Recent Deviation: %d%%",
                        (int) (100 * Timer.getPercentDeviation())
                ),
                horizontalCenterPos, circleYPos[4] + verticalAfterImageOffset, paint
        );

        //local handling the overall step regularity
        int localTotal = 1;
        if (MainActivity.getTotalStepCount() != 0) {
            localTotal = MainActivity.getTotalStepCount();
        }
        canvas.drawText(
                String.format(Locale.getDefault(),
                        "Overall Regularity: %d%%",
                        (100 * (localTotal - MainActivity.getTotalNonConsecutiveStepCount())
                                / localTotal )
                ),
                horizontalCenterPos, circleYPos[4] + verticalAfterImageOffset + textSize, paint
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
