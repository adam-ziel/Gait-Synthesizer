package com.example.ziela.gaitsynthesizer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class InputNoteDetector extends AppCompatActivity implements View.OnTouchListener,
        SeekBar.OnSeekBarChangeListener {

    protected TextView textView;
    protected SeekBar seekBar;

    @Override
    /*
     * This method is used for detecting touches,
     * and distinguishing between presses and releases
     */
    public boolean onTouch( View v, MotionEvent event ) {
        if( event.getAction() == MotionEvent.ACTION_DOWN ) {
            InputActivity.onNoteInput();
            Intent config = new Intent( InputNoteDetector.this, ConfigActivity.class );
            InputNoteDetector.this.startActivity( config );
            finish();
        }
        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int seekBarValue, boolean fromUser) {
        InputActivity.onNoteInput();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

}
