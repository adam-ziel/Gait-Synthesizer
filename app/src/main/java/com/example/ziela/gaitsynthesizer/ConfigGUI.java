package com.example.ziela.gaitsynthesizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Movie;
import android.view.View;

import java.io.InputStream;

public class ConfigGUI extends View {

    private Paint paint;
    private Movie movie;
    private long startTime = 0;

    public ConfigGUI(Context context){
        super(context);
        InputStream inputStream = getResources().openRawResource( R.drawable.loading );
        movie = Movie.decodeStream( inputStream );
    }

    @Override
    /*
     * Draws GUI using canvas rather than XML files
     */
    protected void onDraw( Canvas canvas ) {
        long currTime = android.os.SystemClock.uptimeMillis();
        if( startTime == 0 )
            startTime = currTime;
        int playTime = (int)( (currTime - startTime)%movie.duration() );
        movie.setTime( playTime );
        movie.draw( canvas, 100, 100 );
        canvas.drawColor( Color.WHITE );
        invalidate(); // redraw canvas
    }
}
