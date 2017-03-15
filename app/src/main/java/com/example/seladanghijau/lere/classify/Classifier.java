package com.example.seladanghijau.lere.classify;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.seladanghijau.lere.callback.AsyncTaskCallback;
import com.example.seladanghijau.lere.featureextract.FeatureExtractor;
import com.example.seladanghijau.lere.preprocess.PreProcessor;

public class Classifier {
    private PreProcessor preProcessor;
    private FeatureExtractor featureExtractor;

    public Classifier(final Context context) {
        preProcessor = new PreProcessor(context);
        featureExtractor = new FeatureExtractor(context, new AsyncTaskCallback() {
            @Override
            public void callback() {
                Toast.makeText(context, "Feature extraction ended", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * pre-condition: doPreProcessing() & doFeatureExtract()
     * post-condition: output result
     */
    // TODO: 3/15/2017
    public void classify() {
        Log.d("Debug: Classifier", "Do pre-processing");
        new StartClassify().execute();

        Log.d("Debug: Classifier", "Do classify");
    }

    private class StartClassify extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            preProcessor.initPreProcess();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            featureExtractor.execute();
        }
    }
}
