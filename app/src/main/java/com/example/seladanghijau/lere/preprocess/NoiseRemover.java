package com.example.seladanghijau.lere.preprocess;

import android.media.audiofx.NoiseSuppressor;
import android.util.Log;


public class NoiseRemover {
    private int audioRecordSession;

    public NoiseRemover(int audioRecordSession) {
        this.audioRecordSession = audioRecordSession;
    }

    /**
     * check wheter the phone support NoiseSuppressor or not.
     * @return boolean - true=support, false=not support
     */
    private boolean checkSupport() {
        return NoiseSuppressor.isAvailable();
    }

    /**
     * output an error message to log console
     * @param errorMessage - message to log console
     */
    private void outputErrorLog(String errorMessage) {
        Log.e("Noise Remover: ", errorMessage);
    }

    /**
     * initialize noise suppressor for the recording session.
     */
    private void initNoiseSuppressor() {
        NoiseSuppressor noiseSuppressor;

        noiseSuppressor = NoiseSuppressor.create(audioRecordSession);

        if(noiseSuppressor.getEnabled())
            Log.d("Noise Remover: ", "NoiseSuppressor initialized");
    }

    /**
     * pre-condition: NoiseSuppressor must be supported
     * post-condition: initNoiseSuppressor()
     */
    public void removeNoise() {
        if(!checkSupport())
            outputErrorLog("noise Suppressor not supported");
        else
            initNoiseSuppressor();
    }
}
