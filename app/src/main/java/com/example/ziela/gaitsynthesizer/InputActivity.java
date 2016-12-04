package com.example.ziela.gaitsynthesizer;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

/**
 *
 */
public class InputActivity extends InputNoteDetector {
    public static final int NOTE_OFFSET = 45; // scaling factor since SeekBar starts at 0
    private static FrequencyBuffer notePreview;
    private static int inputMIDINote;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        // create fileGUI and listener objects
        setContentView( R.layout.activity_input );
        textView = (TextView) findViewById( R.id.inputNoteTextView );
        seekBar = (SeekBar) findViewById( R.id.seekBar );
        View view = findViewById( R.id.bypassButton );
        if( view != null )
            view.setOnTouchListener( this );

        getNoteValue();
        createBufferAndPlay();
        seekBar.setOnSeekBarChangeListener( this );
    }

    /**
     * Transition to next activity, and kill this one
     */
    public static void onNoteInput(){
        stopBufferAndDeallocate();
        createBufferAndPlay();
    }

    /**
     * Interprets note from SeekBar value, then displays its value
     */
    public void getNoteValue() {
        inputMIDINote = seekBar.getProgress() + NOTE_OFFSET;
        textView.setText( String.format( Locale.getDefault(), "The starting note will be %d",
                                         inputMIDINote ) );
    }

    /**
     * Constructs new FrequencyBuffer at frequency corresponding to inputNote,
     * then plays this buffer
     */
    public static void createBufferAndPlay() {
        notePreview = new FrequencyBuffer( MainActivity.midiToFrequency( inputMIDINote ) );
        notePreview.play();
    }

    public static void stopBufferAndDeallocate() {
        notePreview.stop();
        notePreview.destroy();
    }

    public static int getInputNote() {
        return inputMIDINote;
    }

}