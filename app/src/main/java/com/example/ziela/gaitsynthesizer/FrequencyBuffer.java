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
    private AudioTrack audioTrack;
    private double[] waveTable;
    private short[] noteBuffer;
    private double frequency;
    private static int sampleRateInHz = 44100 / 4; // cut as low as possible
    private static int bufferSize = AudioTrack.getMinBufferSize(sampleRateInHz,
                                                         AudioFormat.CHANNEL_OUT_MONO,
                                                         AudioFormat.ENCODING_PCM_8BIT);

    /**
     * Constructs FrequencyBuffer instance at supplied frequency
     *
     * @param frequency
     */
    public FrequencyBuffer(double frequency) {
        //Log.d("BUFFSIZE", Integer.toString(bufferSize));
        this.frequency = frequency;
        initializeAudioTrack();
        buildWave();
        writeWaveToAudioTrack();
    }

    /**
     *
     */
    public void initializeAudioTrack() {
//        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz,
//                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
//                bufferSize * 25, AudioTrack.MODE_STATIC);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                sampleRateInHz * 2, AudioTrack.MODE_STATIC);
        audioTrack.setVolume(AudioTrack.getMaxVolume());
    }

    /**
     * Instantiate all of the sound values.
     */
    public void buildWave() {
        waveTable = new double[sampleRateInHz];
        noteBuffer = new short[sampleRateInHz];
//        double dTheta = 2.0 * Math.PI * frequency / 44100;
//        double theta = 0;

        for (int i = 0; i < waveTable.length; i++) {
//            waveTable[i] = Math.sin(theta);
//            waveTable[i] = Math.sin((2.0 * Math.PI * i / (sampleRateInHz / frequency)));
            waveTable[i] = Math.sin( i * 2.0 * Math.PI * frequency / sampleRateInHz );
            noteBuffer[i] = (short) (waveTable[i] * Short.MAX_VALUE);
            //theta += dTheta;
        }
    }

    /**
     * Because we are statically playing and not streaming, I only want to write the buffer once
     */
    public void writeWaveToAudioTrack() {
        audioTrack.write(noteBuffer, 0, noteBuffer.length);
        audioTrack.setLoopPoints(0, noteBuffer.length, -1);
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
        audioTrack.setLoopPoints(0, noteBuffer.length, -1);
    }
    /**
     * Frees the resources related to the audio track.
     * Also idk how to delete the object in java. Usually its pretty good tho
     */
    protected void destroy() {
        audioTrack.release();
    }
}