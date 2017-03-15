package com.example.seladanghijau.lere;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seladanghijau.lere.callback.AsyncTaskCallback;
import com.example.seladanghijau.lere.classify.Classifier;
import com.example.seladanghijau.lere.featureextract.FeatureExtractor;
import com.example.seladanghijau.lere.preprocess.PreProcessor;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView txtStatus;
    Button btnRecord;

    private PreProcessor preProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initListener();
        setup();
    }

    private void setup() {
        Log.d("Debug: MainActivity", "setup()");

        preProcessor = new PreProcessor(this);
    }

    private void initViews() {
        Log.d("Debug: MainActivity", "initViews()");

        txtStatus = (TextView) findViewById(R.id.txtStatus);
        btnRecord = (Button) findViewById(R.id.btnRecord);
    }

    private void initListener() {
        Log.d("Debug: MainActivity", "initListener()");

        txtStatus.setOnClickListener(this);
        btnRecord.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRecord:
                preProcessor.initPreProcess();
                break;
        }
    }
}
