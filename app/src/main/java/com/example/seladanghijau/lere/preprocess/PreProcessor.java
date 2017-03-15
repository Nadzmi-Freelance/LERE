package com.example.seladanghijau.lere.preprocess;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.seladanghijau.lere.callback.AsyncTaskCallback;
import com.example.seladanghijau.lere.entity.Record;
import com.example.seladanghijau.lere.featureextract.FeatureExtractor;

import java.util.ArrayList;


public class PreProcessor {
    private AudioRecorder audioRecorder;
    private Context context;

    public PreProcessor(final Context context) {
        this.context = context;

        audioRecorder = new AudioRecorder(context, new AsyncTaskCallback() {
            @Override
            public void callback() {
                Toast.makeText(context, "Recording ended", Toast.LENGTH_SHORT).show();

                StartPreProcess startProcess = new StartPreProcess(context);
                startProcess.execute();
            }
        });

        Log.d("Debug: PreProcessor", "Initialized");
    }

    public void initPreProcess() {
        Log.d("Debug: PreProcessor", "Execute AudioRecorder");

        audioRecorder.execute();
    }

    private void prepareData() {
        Record prepareRecord;
        ArrayList<Short> rawDataList;
        ArrayList<Double> convertedDataList;

        try {
            convertedDataList = new ArrayList<>();
            rawDataList = audioRecorder.getPCMData();

            for(int x=0 ; x<rawDataList.size() ; x++)
                convertedDataList.add((double) rawDataList.get(x));

            prepareRecord = new Record("test", context);
            prepareRecord.saveToData(convertedDataList);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private class StartPreProcess extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialogSave;
        private Context context;

        public StartPreProcess(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialogSave = new ProgressDialog(context);

            dialogSave.setCancelable(false);
            dialogSave.setMessage("Processing audio...");

            dialogSave.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            FeatureExtractor featureExtractor;

            featureExtractor = new FeatureExtractor(context, new AsyncTaskCallback() {
                @Override
                public void callback() {
                    Toast.makeText(context, "Feature extraction ended", Toast.LENGTH_LONG).show();
                }
            });

            if(dialogSave.isShowing())
                dialogSave.dismiss();

            // featureExtractor.execute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Record saveAudio;

                saveAudio = new Record("test", context);

                Log.d("Debug: PreProcessor", "Saving PCM data");
                saveAudio.saveToPCM(audioRecorder.getPCMData());
                Log.d("Debug: PreProcessor", "PCM data saved");

                Log.d("Debug: PreProcessor", "Preparing data");
                prepareData();
                Log.d("Debug: PreProcessor", "Data prepared");
            } catch (Exception e) { e.printStackTrace(); }

            return null;
        }
    }
}
