package com.example.ziela.gaitsynthesizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;

public class ConfigurationGUI extends View {

    private Movie movie;
    private long startTime = 0;

    public ConfigurationGUI(Context context ){
        super( context );
        initializeView();
    }

    public ConfigurationGUI(Context context, AttributeSet attrs ){
        super( context, attrs );
        initializeView();
    }

    public ConfigurationGUI(Context context, AttributeSet attrs, int defStyle ) {
        super(context, attrs, defStyle);
        initializeView();
    }

    private void initializeView(){
        InputStream inputStream = getResources().openRawResource( R.raw.loading );
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
        if( movie != null ) {
            int playTime = (int)( (currTime - startTime)%movie.duration() );
            movie.setTime( playTime );
            canvas.scale( (float) 4.5, (float) 4.5 );
            movie.draw( canvas, 0, 0 );
            invalidate(); // redraw canvas
        }
    }
}
