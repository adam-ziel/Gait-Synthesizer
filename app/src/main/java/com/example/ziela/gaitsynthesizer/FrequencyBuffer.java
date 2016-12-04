package com.example.ziela.gaitsynthesizer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class FrequencyBuffer {
    private AudioTrack audioTrack;
    private double[] waveTable;
    private short[] noteBuffer;
    private double frequency;
    private static final int sampleRateInHz = ( 44100 / 4 ); // cut as low as possible

    /**
     * Constructs FrequencyBuffer instance at supplied frequency
     *
     * @param frequency
     */
    protected FrequencyBuffer( double frequency ) {
        this.frequency = frequency;
        initializeAudioTrack();
        buildWave();
        writeWaveToAudioTrack();
    }

    /**
     *
     */
    protected void initializeAudioTrack() {
        audioTrack = new AudioTrack( AudioManager.STREAM_MUSIC,
                                     sampleRateInHz,
                                     AudioFormat.CHANNEL_OUT_MONO,
                                     AudioFormat.ENCODING_PCM_16BIT,
                                     sampleRateInHz * 2,
                                     AudioTrack.MODE_STATIC);
        audioTrack.setVolume( AudioTrack.getMaxVolume() );
    }

    /**
     * Instantiate all of the sound values.
     */
    protected void buildWave() {
        waveTable = new double[ sampleRateInHz ];
        noteBuffer = new short[ sampleRateInHz ];
        for (int i = 0; i < waveTable.length; i++) {
            waveTable[ i ] = Math.sin( i * 2.0 * Math.PI * frequency / sampleRateInHz );
            noteBuffer[ i ] = (short)( waveTable[ i ] * Short.MAX_VALUE );
        }
    }

    /**
     * Because we are statically playing and not streaming, I only want to write the buffer once
     */
    protected void writeWaveToAudioTrack() {
        audioTrack.write( noteBuffer, 0, noteBuffer.length );
        audioTrack.setLoopPoints( 0, noteBuffer.length, -1 );
    }

    /**
     * Play the selected track
     */
    public void play() {
        audioTrack.play();
    }

    /**
     * Stop playing the selected track, and reset position in buffer to index 0
     */
    public void stop() {
        audioTrack.stop();
        audioTrack.reloadStaticData();
        audioTrack.setLoopPoints( 0, noteBuffer.length, -1 );
    }

    /**
     * Frees the resources related to the audio track.
     * Also idk how to delete the object in java. Usually its pretty good tho
     */
    protected void destroy() {
        audioTrack.release();
    }
}