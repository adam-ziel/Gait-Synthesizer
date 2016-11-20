package com.example.ziela.gaitsynthesizer;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import android.os.SystemClock;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import android.media.AudioTrack;
import android.media.AudioFormat;
import android.media.AudioManager;

public class MainActivity extends AppCompatActivity
        implements OnTouchListener, SensorEventListener {


    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private int rootNote = 57; // note as MIDI number (C4?)

    private int[] majorScaleSteps = {0, 2, 4, 5, 7, 9, 11, 12};

    private int[] minorScaleSteps = {0, 2, 3, 5, 7, 8, 10, 12};

    private double[] scaleFrequencies = populateScale(rootNote,
            majorScaleSteps);

    private FrequencyBuffer note1 = new FrequencyBuffer(scaleFrequencies[0]);
    private FrequencyBuffer note2 = new FrequencyBuffer(scaleFrequencies[1]);
    private FrequencyBuffer note3 = new FrequencyBuffer(scaleFrequencies[2]);
    private FrequencyBuffer note4 = new FrequencyBuffer(scaleFrequencies[3]);
    private FrequencyBuffer note5 = new FrequencyBuffer(scaleFrequencies[4]);
    private FrequencyBuffer note6 = new FrequencyBuffer(scaleFrequencies[5]);
    private FrequencyBuffer note7 = new FrequencyBuffer(scaleFrequencies[6]);
    private FrequencyBuffer note8 = new FrequencyBuffer(scaleFrequencies[7]);

    private FrequencyBuffer[] bufferPool = {note1, note2, note3, note4,
                                            note5, note6, note7, note8};

    private int count = 0;

    private TextView textView;

    boolean firstStep = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        //TextView tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText(stringFromJNI());


        View layoutMain = findViewById(R.id.layoutMain);

        // Set on touch listener
        View v;

        v = findViewById(R.id.button1);
        verifyAndSetOnTouchListener(v);

        v = findViewById(R.id.button2);
        verifyAndSetOnTouchListener(v);

        v = findViewById(R.id.button3);
        verifyAndSetOnTouchListener(v);

        v = findViewById(R.id.button4);
        verifyAndSetOnTouchListener(v);

        v = findViewById(R.id.button5);
        verifyAndSetOnTouchListener(v);

        v = findViewById(R.id.button6);
        verifyAndSetOnTouchListener(v);

        v = findViewById(R.id.button7);
        verifyAndSetOnTouchListener(v);

        v = findViewById(R.id.button8);
        verifyAndSetOnTouchListener(v);

        textView = (TextView) findViewById(R.id.mainSteps);

    }

    @Override
    /*
     * This method is used for detecting touches,
     * and distinguishing between presses and releases
     */
    public boolean onTouch(View v, MotionEvent event)
    {
        int action = event.getAction();
        int id = v.getId();

        switch (action)
        {
            case MotionEvent.ACTION_DOWN: // Button is held down, start playing the music

                playById(id);
                break;

            case MotionEvent.ACTION_UP: // Button has been released. Stop playing the music

                stopById(id);
                break;

            default:
                return false;
        }

        return false;
    }

    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float[] values = event.values;
        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }

        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            // For test only. Only allowed value is 1.0 i.e. for step taken

            if (!firstStep)
                bufferPool[(count-1)%8].stop();

            bufferPool[count%8].play();

            this.count++;
            textView.setText("Step Detector Detected : " + count);

            firstStep = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Iterates through 8-entry frequency array, populating with
     * scale degrees based on scaleStep array
     */
    public static double[] populateScale(int rootNote, int[] scaleSteps)
    {
        double[] scaleFrequencies = new double[8];

        for (int i = 0; i < 8; i++)
        {
            scaleFrequencies[i] = midiNoteToFrequency(rootNote + scaleSteps[i]);
        }

        return scaleFrequencies;
    }

    /**
     * Retuns frequency from input integer MIDI note
     */
    public static double midiNoteToFrequency(int midiNote)
    {
        return Math.pow(2, (double) (midiNote - 69) / 12) * 440;
    }


    public void verifyAndSetOnTouchListener(View v)
    {
        if (v != null)
            v.setOnTouchListener(this);
    }

    /**
     * Begins playing buffer based on button id
     */
    public void playById(int id)
    {
        switch (id)
        {
            case R.id.button1:
                note1.play();
                break;
            case R.id.button2:
                note2.play();
                break;
            case R.id.button3:
                note3.play();
                break;
            case R.id.button4:
                note4.play();
                break;
            case R.id.button5:
                note5.play();
                break;
            case R.id.button6:
                note6.play();
                break;
            case R.id.button7:
                note7.play();
                break;
            case R.id.button8:
                note8.play();
                break;
            default:
                return;
        }
    }

    /**
     * Stops buffer based on button id
     */
    public void stopById(int id)
    {
        switch (id)
        {
            case R.id.button1:
                note1.stop();
                break;
            case R.id.button2:
                note2.stop();
                break;
            case R.id.button3:
                note3.stop();
                break;
            case R.id.button4:
                note4.stop();
                break;
            case R.id.button5:
                note5.stop();
                break;
            case R.id.button6:
                note6.stop();
                break;
            case R.id.button7:
                note7.stop();
                break;
            case R.id.button8:
                note8.stop();
                break;
            default:
                return;
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
