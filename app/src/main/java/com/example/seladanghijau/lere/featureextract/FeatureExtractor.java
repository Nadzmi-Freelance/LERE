package com.example.seladanghijau.lere.featureextract;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.seladanghijau.lere.callback.AsyncTaskCallback;
import com.example.seladanghijau.lere.preprocess.AudioRecorder;


public class FeatureExtractor extends AsyncTask<Void, Void, Void> {
    private static final int FFT_SIZE = 512;
    private static final int NUM_COEFF = 19;
    private static final int MEL_BANDS = 20;
    private static final double FREQUENCY = AudioRecorder.getFrequency();

    private MFCC mfcc;
    private ProgressDialog progressDialog;
    private AsyncTaskCallback featureExtractorCallback;

    double fftBufferR[] = new double[FFT_SIZE];
    double fftBufferI[] = new double[FFT_SIZE];

    public FeatureExtractor(Context context, AsyncTaskCallback featureExtractorCallback) {
        this.featureExtractorCallback = featureExtractorCallback;

        mfcc = new MFCC(FFT_SIZE, NUM_COEFF, MEL_BANDS, FREQUENCY);
        progressDialog = new ProgressDialog(context);
    }

    /**
     * Extract feature from converted pcm data
     */
    // TODO: 3/15/2017
    private void extractFeature() {
        double[] cepstrum = mfcc.cepstrum(fftBufferR, fftBufferI);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setMessage("Extracting feature...");
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(progressDialog.isShowing())
            progressDialog.dismiss();

        featureExtractorCallback.callback();
    }

    @Override
    protected Void doInBackground(Void... params) {
        extractFeature();

        return null;
    }
}
