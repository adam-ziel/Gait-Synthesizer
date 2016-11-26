package com.example.ziela.gaitsynthesizer;

/**
 * Created by ziela on 11/20/16.
 */

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by wigleyd on 11/13/2016.
 */

public class FrequencyBuffer {

    private double frequency;

    private int sampleRateInHz = 44100; // cut as low as possible

    public AudioTrack audioTrack;

    private double[] waveTable;

    public short[] noteBuffer;

    public int length;


    /**
     * Determine buffer size
     * Construct audioTrack
     * Write tracks
     *
     /**
     * Constructor used for unspecified volume amount
     * @param frequency
     */
    public FrequencyBuffer(double frequency)
    {
        this.frequency = frequency;

        int mBufferSize = AudioTrack.getMinBufferSize(sampleRateInHz,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_8BIT);

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                mBufferSize * 25, AudioTrack.MODE_STATIC);

        audioTrack.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume());

        instantiateValues();

        writeTheBuffer();
    }

//    /**
//     * Constructor used for specifiying a volume
//     * @param frequency Frequency this buffer will output
//     * @param intervals signal quality of the wave. More intervals is higher quality. Imagine a rahemian sum
//     * @param leftVolume volume amount for the left speaker
//     * @param rightVolume volume amount for the right speaker
//     */
//    public FrequencyBuffer(double frequency, int intervals, float leftVolume, float rightVolume){
//
//        this.frequency = frequency;
//        this.sampleRateInHz  = intervals;
//
//        int mBufferSize = AudioTrack.getMinBufferSize(intervals,
//                AudioFormat.CHANNEL_OUT_MONO,
//                AudioFormat.ENCODING_PCM_8BIT);
//
//        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, intervals,
//                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
//                mBufferSize, AudioTrack.MODE_STREAM);
//
//        setVolume(leftVolume,rightVolume);
//        instantiateValues();
//        writeTheBuffer();
//    }

    /**
     * Instantiate all of the sound values.
     */
    private void instantiateValues()
    {
        waveTable = new double[sampleRateInHz];
        noteBuffer = new short[sampleRateInHz];

        for (int i = 0; i < waveTable.length; i++)
        {
            waveTable[i] = Math.sin((2.0 * Math.PI * i / (sampleRateInHz / frequency)));

            noteBuffer[i] = (short) (waveTable[i] * Short.MAX_VALUE);
        }

        audioTrack.write(noteBuffer, 0, noteBuffer.length);
        audioTrack.setLoopPoints(0, noteBuffer.length, -1);
    }

    /**
     * Play the selected track
     */
    public void play()
    {
        audioTrack.play();
    }

    /**
     * Because we are statically playing and not streaming, I only want to write the buffer once
     */
    private void writeTheBuffer()
    {
        audioTrack.write(noteBuffer, 0, noteBuffer.length);
    }

    /**
     * Stop playing the selected track
     */
    public void stop() {
        //basically a redundancy check to ensure that we are playing in the first play
        if (AudioTrack.PLAYSTATE_PLAYING == audioTrack.getPlayState()) {
            audioTrack.stop();
            audioTrack.reloadStaticData();
            audioTrack.setLoopPoints(0, noteBuffer.length, -1);

        }else {
            System.out.println("You have tried to stop a track that was not playing and I stopped you");
        }
    }

    /**
     * The volume is going to be set to a predefined value
     * @param leftVolume volume amount for the left speaker
     * @param rightVolume volume amount for the right speaker
     */
    private void setVolume(float leftVolume, float rightVolume)
    {
        audioTrack.setStereoVolume(leftVolume, rightVolume);
    }

    /**
     * Method that simply sets the volume to the max
     */
    private void setVolume()
    {
        audioTrack.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume());
    }

    /**
     * Frees the resources related to the audio track.
     * Also idk how to delete the object in java. Usually its pretty good tho
     */
    protected void destroy()
    {
        audioTrack.release();
    }

}
