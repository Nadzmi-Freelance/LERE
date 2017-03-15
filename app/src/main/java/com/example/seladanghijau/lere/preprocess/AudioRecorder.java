package com.example.seladanghijau.lere.preprocess;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;


import com.example.seladanghijau.lere.callback.AsyncTaskCallback;

import java.util.ArrayList;


public class AudioRecorder extends AsyncTask<Void, Void, Void> {
    private static final int FREQUENCY = 44100;
    private static final int CHANNEL_CONF = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int MEDIA_RECORDER = MediaRecorder.AudioSource.VOICE_COMMUNICATION;

    private int bufferSize;
    private boolean recording;
    private AudioRecord audioRecord;
    private ArrayList<Short> pcmData;
    private AsyncTaskCallback recorderCallback;
    private ProgressDialog progressDialog;

    public AudioRecorder(Context context, AsyncTaskCallback recorderCallback) {
        this.recorderCallback = recorderCallback;

        recording = false;
        bufferSize = AudioRecord.getMinBufferSize(FREQUENCY, CHANNEL_CONF, AUDIO_FORMAT);

        progressDialog = new ProgressDialog(context);
        audioRecord = new AudioRecord(MEDIA_RECORDER, FREQUENCY, CHANNEL_CONF, AUDIO_FORMAT, bufferSize);

        Log.d("Debug: AudioRecorder", "Initialized");
    }

    public static int getFrequency() { return FREQUENCY; }

    public boolean isRecording() { return recording; }
    public ArrayList<Short> getPCMData() { return pcmData; }

    /**
     * start the recording process.
     * Save each of the short data into 'dataList'
     */
    private void startRecord() {
        short[] buffer;
        NoiseRemover noiseRemover;

        recording = true;
        pcmData = new ArrayList<>();
        buffer = new short[bufferSize];
        noiseRemover = new NoiseRemover(audioRecord.getAudioSessionId());

        Log.d("Debug: AudioRecorder", "Start recording");
        audioRecord.startRecording();
        noiseRemover.removeNoise();
        while (recording) {
            int bufferRead;

            bufferRead = audioRecord.read(buffer, 0, bufferSize); // read audio data
            for(int x=0 ; x<bufferRead ; x++)
                pcmData.add(buffer[x]); // save audio data temporarily
        }
    }

    /**
     * stop the recording process.
     * Save 'dataList' into a .pcm file
     */
    private void stopRecord() {
        recording = false;

        if(audioRecord != null) {
            audioRecord.stop(); // stop record
            audioRecord.release(); // release record buffer
            audioRecord = null;
        }

        Log.d("Debug: AudioRecorder", "Recording stopped");
    }

    @Override
    protected void onPreExecute() {
        Log.d("Debug: AudioRecorder", "Pre Execute");

        progressDialog.setMessage("Recording");
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Stop", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopRecord();
            }
        });

        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.d("Debug: AudioRecorder", "Post Execute");

        if(progressDialog.isShowing())
            progressDialog.dismiss();

        recorderCallback.callback();
    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d("Debug: AudioRecorder", "Do In Background");

        startRecord();

        return null;
    }
}
