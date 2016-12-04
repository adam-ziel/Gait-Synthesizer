package com.example.ziela.gaitsynthesizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

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
        instantiateCircleCoordinates(); // populate coordinate arrays
        paint = new Paint();
        paint.setTextSize( 80 );
    }

    @Override
    /*
     * Draws GUI using canvas rather than XML files
     */
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        for (int i = 0; i < 8; i ++){
            paint.setColor(Color.GRAY); // Gray circles indicate non-active tones
            if (i != ((MainActivity.getStepCount())%8)) {
                paint.setColor(Color.GRAY); // Reset all non-active tones to gray
                canvas.drawCircle(circleXPos[i], circleYPos[i], radius, paint);
            }else {
                paint.setColor(Color.GREEN); // Set active tone to green
                canvas.drawCircle(circleXPos[i], circleYPos[i], radius, paint);
                paint.setColor(Color.GRAY);
            }
        }
        canvas.drawText("Touch anywhere to play", 100, 80, paint);
        canvas.drawText( String.format("Steps detected: %d", MainActivity.getStepCount() ),
                         100, 200, paint);
        canvas.drawText( String.format("Timer 1: %d", (int) Timer.getTimer1() ),
                         80, circleYPos[4] + 300, paint);
        canvas.drawText( String.format("Timer 2: %d", (int) Timer.getTimer2() ),
                        860, circleYPos[4] + 300, paint);
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
    private void instantiateCircleCoordinates(){
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
