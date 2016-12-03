package com.example.ziela.gaitsynthesizer;

/**
 * Created by wigleyd on 11/28/2016.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class MainGUI extends View {

    private Paint paint;

    private static final int radius = 120;

    private float[] circleXPos = new float[8];
    private float[] circleYPos = new float[8];

    private static final float START_X_POS = 700;
    private static final float START_Y_POS = 620;

    private static final float X_POS_OFFSET = 200;
    private static final float Y_POS_OFFSET = X_POS_OFFSET;


    public MainGUI(Context context) {
        super(context);
        instantiateCircleCoordinates();

        // create the Paint object
        paint = new Paint();
    }

    @Override
    /*
     * Creating my own onDraw method to flag to android to draw the screen using this logic rather
      * than XML stuff
     */
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(Color.WHITE);

        for (int i = 0; i < 8; i ++){
            paint.setColor(Color.GRAY);
            if (i != (MainActivity.getStepCount()%8)) {
                paint.setColor(Color.GRAY); // Set the color back to gray.
                                            // We are not on this circle
                canvas.drawCircle(circleXPos[i], circleYPos[i], radius, paint);
            }else {
                paint.setColor(Color.GREEN); //set color to green to show this circle is different.
                                             //Represents our current tone
                canvas.drawCircle(circleXPos[i], circleYPos[i], radius, paint);
            }
        }
        //can draw text in this as well if we want metrics or other text stuff
        invalidate(); //Tell Android the area needs to be redrawn
    }

    /**
     * Method that handles defining pixel coordinates on 8 circles
     * Prepare to get confused. Im going to draw you a pic just so its easier to follow
     *      O       THIS HAS THE SAME X AS BOTTOM AND ITS OWN Y
     *    O  O      THESE HAVE SAME Y AND SAME X AS SIMILAR ROW AT BOTTOM
     *   O    O     THESE HAVE SAME Y AND THEIR OWN X
     *    O  O      THESE HAVE SAME Y AND SAME X AS SIMILAR ROW AT TOP
     *      O       THIS HAS THE SAME X AS TOP AND ITS OWN Y
     */
    private void instantiateCircleCoordinates(){
        //x coordinates
        circleXPos[0] = START_X_POS; //top circle
        circleXPos[1] = START_X_POS + X_POS_OFFSET; // top right
        circleXPos[2] = START_X_POS + 2 * X_POS_OFFSET; //far right
        circleXPos[3] = circleXPos[1]; //bottom right
        circleXPos[4] = circleXPos[0]; //bottom circle
        circleXPos[5] = circleXPos[0] - X_POS_OFFSET; //bottom left
        circleXPos[6] = circleXPos[0] - 2 * X_POS_OFFSET; //far left
        circleXPos[7] = circleXPos[5]; //top left

        //y coordinates
        circleYPos[0] = START_Y_POS; //top circle
        circleYPos[1] = START_Y_POS + Y_POS_OFFSET; //top right circle
        circleYPos[2] = START_Y_POS + 2 * Y_POS_OFFSET; //far right circle
        circleYPos[3] = START_Y_POS + 3 * Y_POS_OFFSET; //bottom right circle
        circleYPos[4] = START_Y_POS + 4 * Y_POS_OFFSET; //bottom circle
        circleYPos[5] = circleYPos[3]; //bottom left y pos is equal to bottom right y pos
        circleYPos[6] = circleYPos[2]; //far left y pos is far right y pos
        circleYPos[7] = circleYPos[1]; //top left y pos is top right y pos
    }
}
