package com.example.ziela.gaitsynthesizer;

/**
 * Created by ziela on 11/20/16.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.view.View.OnTouchListener;


/**
 *
 */
public class InputActivity extends AppCompatActivity implements OnTouchListener
{
    public static final int NOTE_OFFSET = 45; // scaling factor since SeekBar starts at 0

    private FrequencyBuffer notePreview;

    private TextView inputNoteDisplay;

    private SeekBar noteSelectBar;

    private static int inputMIDINote;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        getXMLHandles();

        getNoteValue();

        createBufferAndPlay();

        noteSelectBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int seekBarValue, boolean fromUser)
            {
                getNoteValue();

                stopBufferAndDeallocate();

                createBufferAndPlay();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }


    @Override
    /*
     * This method is used for detecting touches,
     * and distinguishing between presses and releases
     */
    public boolean onTouch(View v, MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            transitionToCalibrationActivity();
        }

        return false;
    }


    /**
     * Retrieves handles to all XML elements we need to modify
     */
    public void getXMLHandles()
    {
        inputNoteDisplay = (TextView) findViewById(R.id.inputNoteTextView);

        noteSelectBar = (SeekBar) findViewById(R.id.seekBar);

        View v = findViewById(R.id.bypassButton);

        if (v != null)
            v.setOnTouchListener(this);
    }


    /**
     * Interprets note from SeekBar value, then displays its value
     */
    public void getNoteValue()
    {
        inputMIDINote = noteSelectBar.getProgress() + NOTE_OFFSET;

        inputNoteDisplay.setText("The starting note will be " + inputMIDINote);
    }


    /**
     * Constructs new FrequencyBuffer at frequency corresponding to inputNote,
     * then plays this buffer
     */
    public void createBufferAndPlay()
    {
        notePreview = new FrequencyBuffer(MainActivity.midiNoteToFrequency(inputMIDINote));
        notePreview.play();
    }


    public void stopBufferAndDeallocate()
    {
        notePreview.stop();
        notePreview.destroy();
    }


    public static int getInputNote()
    {
        return inputMIDINote;
    }


    /**
     * Transition to next activity, and kill this one
     */
    public void transitionToCalibrationActivity()
    {
        stopBufferAndDeallocate();

        Intent configuration = new Intent(InputActivity.this, ConfigurationActivity.class);

        InputActivity.this.startActivity(configuration);

        finish();
    }
}