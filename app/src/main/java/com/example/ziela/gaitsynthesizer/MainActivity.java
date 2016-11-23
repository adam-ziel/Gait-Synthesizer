package com.example.ziela.gaitsynthesizer;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity
        implements SensorEventListener {


//    private int rootNote = 57; // note as MIDI number (C4?)
//
//    private int[] majorScaleSteps = {0, 2, 4, 5, 7, 9, 11, 12};
//
//    private int[] minorScaleSteps = {0, 2, 3, 5, 7, 8, 10, 12};
//
//    private double[] scaleFrequencies = populateScale(rootNote,
//            majorScaleSteps);
//
//    private FrequencyBuffer note1 = new FrequencyBuffer(scaleFrequencies[0]);
//    private FrequencyBuffer note2 = new FrequencyBuffer(scaleFrequencies[1]);
//    private FrequencyBuffer note3 = new FrequencyBuffer(scaleFrequencies[2]);
//    private FrequencyBuffer note4 = new FrequencyBuffer(scaleFrequencies[3]);
//    private FrequencyBuffer note5 = new FrequencyBuffer(scaleFrequencies[4]);
//    private FrequencyBuffer note6 = new FrequencyBuffer(scaleFrequencies[5]);
//    private FrequencyBuffer note7 = new FrequencyBuffer(scaleFrequencies[6]);
//    private FrequencyBuffer note8 = new FrequencyBuffer(scaleFrequencies[7]);
//
//    private FrequencyBuffer[] bufferPool = {note1, note2, note3, note4,
//                                            note5, note6, note7, note8};
//
    private int count = 0;

    private TextView textView;

    boolean firstStep = true;

    private SensorManager mSensorManager;
    private Sensor mStepDetectorSensor;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.mainSteps);

        mSensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
        mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

    }



    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            // A step has occured we need to play and increment the counter

            //even removing the music playback does not fix latency
//            if (!firstStep)
//                bufferPool[(count-1)%8].stop();
//
//            bufferPool[count%8].play();

            this.count++;
            textView.setText("Step Detector Detected : " + count);

            firstStep = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //System.out.println("The accuracy of the sensor has been changed");
        // IDEALLY WE WANT THIS TO BE IN SENSOR_STATUS_ACCURACY_HIGH. Not sure how to lock it
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



    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
        // this does pull data as fast as possible
    }

    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this, mStepDetectorSensor);
    }
}
