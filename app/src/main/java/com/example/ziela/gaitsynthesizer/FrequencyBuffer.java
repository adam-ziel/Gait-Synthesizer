package com.example.ziela.gaitsynthesizer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;


public class FrequencyBuffer
{
    private AudioTrack audioTrack;

    private double[] waveTable;

    private short[] noteBuffer;

    private double frequency;

    private static int sampleRateInHz = 44100;

    /**
     * Constructs FrequencyBuffer instance at supplied frequency
     *
     * @param frequency Frequency that the tone is outputted at
     */
    public FrequencyBuffer(double frequency)
    {

        this.frequency = frequency;

        initializeAudioTrack();
        buildWave();
        writeWaveToAudioTrack();
    }


    /**
     * Set up the audio track to contact the driver.
     */
    public void initializeAudioTrack()
    {
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                sampleRateInHz * 2, AudioTrack.MODE_STATIC);

        audioTrack.setVolume(AudioTrack.getMaxVolume());
    }


    /**
     * Instantiate all of the sound values.
     */
    public void buildWave()
    {
        waveTable = new double[sampleRateInHz];
        noteBuffer = new short[sampleRateInHz];

        for (int i = 0; i < waveTable.length; i++)
        {
            waveTable[i] = Math.sin((2.0 * Math.PI * i / (sampleRateInHz / frequency)));
            noteBuffer[i] = (short) (waveTable[i] * Short.MAX_VALUE);
        }
    }


    /**
     * Because we are statically playing and not streaming, I only want to write the buffer once
     */
    public void writeWaveToAudioTrack()
    {
        audioTrack.write(noteBuffer, 0, noteBuffer.length);
        audioTrack.setLoopPoints(0, noteBuffer.length, -1);// Not looping anymore as we have cutoffs
    }


    /**
     * Play the selected track
     */
    public void play()
    {
        audioTrack.play();
    }


    /**
     * Stop playing the selected track, and reset position in buffer to index 0
     */
    public void stop()
    {
        audioTrack.stop();
        audioTrack.reloadStaticData();
        audioTrack.setLoopPoints(0, noteBuffer.length, -1); //dont want to loop anymore. Hard cut offs
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