/**
 * Used to manage audio records
 *
 * @author Seladang Hijau
 * @version 1.0
 * @since 14/3/2017
 */

package com.example.seladanghijau.lere.entity;

import android.content.Context;

import com.example.seladanghijau.lere.preprocess.AudioRecorder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class Record {
    private static String FILE_PATH, PCM_PATH, WAV_PATH, DATA_PATH, FEATURE_PATH;

    private String fileName;

    /**
     * constructor
     *
     * @param fileName - name of file
     * @param context - context from activity
     */
    public Record(String fileName, Context context) {
        this.fileName = fileName;

        FILE_PATH = context.getExternalCacheDir().getAbsolutePath();
        PCM_PATH = FILE_PATH + "/pcm";
        WAV_PATH = FILE_PATH + "/wav";
        DATA_PATH = FILE_PATH + "/data";
        FEATURE_PATH = FILE_PATH + "/feature";


        // initialize file & folder
        File pcmFolder, wavFolder, dataFolder, featureFolder;
        File pcmFile, wavFile, dataFile, featureFile;

        // initialize folder
        pcmFolder = new File(PCM_PATH);
        wavFolder = new File(WAV_PATH);
        dataFolder = new File(DATA_PATH);
        featureFolder = new File(FEATURE_PATH);

        // initialize file
        pcmFile = new File(PCM_PATH + "/" + fileName + ".pcm");
        wavFile = new File(WAV_PATH + "/" + fileName + ".wav");
        dataFile = new File(DATA_PATH + "/" + fileName + ".dat");
        featureFile = new File(FEATURE_PATH + "/" + fileName + ".fea");

        // check folder
        if(!pcmFolder.exists())
            pcmFolder.mkdir();
        if(!wavFolder.exists())
            wavFolder.mkdir();
        if(!dataFolder.exists())
            dataFolder.mkdir();
        if(!featureFolder.exists())
            featureFolder.mkdir();

        // check file
        try {
            if(!pcmFile.exists())
                pcmFile.createNewFile();
            if(!wavFile.exists())
                wavFile.createNewFile();
            if(!dataFile.exists())
                dataFile.createNewFile();
            if(!featureFile.exists())
                featureFile.createNewFile();
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * convert .pcm file to .wav file in "/wav"
     */
    private void convertToWAV(int frequency) throws IOException {
        File pcmFile, wavFile;
        byte[] rawData;
        DataInputStream input;

        pcmFile = new File(PCM_PATH + "/" + fileName + ".pcm");
        wavFile = new File(WAV_PATH + "/" + fileName + ".wav");

        input = null;
        rawData = new byte[(int) pcmFile.length()];

        try {
            input = new DataInputStream(new FileInputStream(pcmFile));
            input.read(rawData);
        } finally {
            if (input != null) {
                input.close();
            }
        }

        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(wavFile));

            // WAVE header
            // see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
            writeString(output, "RIFF"); // chunk id
            writeInt(output, 36 + rawData.length); // chunk size
            writeString(output, "WAVE"); // format
            writeString(output, "fmt "); // subchunk 1 id
            writeInt(output, 16); // subchunk 1 size
            writeShort(output, (short) 1); // audio format (1 = PCM)
            writeShort(output, (short) 1); // number of channels
            writeInt(output, frequency); // sample rate
            writeInt(output, frequency * 2); // byte rate
            writeShort(output, (short) 2); // block align
            writeShort(output, (short) 16); // bits per sample
            writeString(output, "data"); // subchunk 2 id
            writeInt(output, rawData.length); // subchunk 2 size
            // Audio data (conversion big endian -> little endian)
            short[] shorts = new short[rawData.length / 2];
            ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
            ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);

            for (short s : shorts) {
                bytes.putShort(s);
            }

            output.write(bytes.array());
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    private static void writeInt(final DataOutputStream output, final int value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
        output.write(value >> 16);
        output.write(value >> 24);
    }

    private static void writeShort(final DataOutputStream output, final short value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
    }

    private static void writeString(final DataOutputStream output, final String value) throws IOException {
        for (int i = 0; i < value.length(); i++)
            output.write(value.charAt(i));
    }

    /**
     * save pcm data to "/pcm" as ".pcm" file
     *
     * @param pcmData - pcm data
     */
    public void saveToPCM(ArrayList<Short> pcmData) throws IOException {
        File pcmFile;
        DataOutputStream dos;

        // initialization
        pcmFile = new File(PCM_PATH + "/" + fileName + ".pcm");
        dos = new DataOutputStream(new FileOutputStream(pcmFile.getAbsolutePath()));

        // data saving
        for(int x=0 ; x<pcmData.size() ; x++) {
            dos.writeShort(pcmData.get(x)); // write each short into the file
            dos.flush();
        }

        // convert saved pcm file to wav file
        convertToWAV(AudioRecorder.getFrequency());
    }

    /**
     * save converted pcm data to "/data" as ".dat" file
     *
     * @param convertedData - converted data
     */
    public void saveToData(ArrayList<Double> convertedData) throws IOException {
        File dataFile;
        DataOutputStream dos;

        // initialization
        dataFile = new File(DATA_PATH + "/" + fileName + ".dat");
        dos = new DataOutputStream(new FileOutputStream(dataFile.getAbsolutePath()));

        // data saving
        for(int x=0 ; x<convertedData.size() ; x++) {
            dos.writeDouble(convertedData.get(x)); // write each short into the file
            dos.flush();
        }
    }

    /**
     * save extracted feature data to "/feature" as ".fea" file
     * @param featureData - feature data to be saved
     */
    // TODO: 3/14/2017
    public void saveToFeature(ArrayList<Double> featureData) throws IOException {
        File featureFile;
        DataOutputStream dos;

        // initialization
        featureFile = new File(FEATURE_PATH + "/" + fileName + ".fea");
        dos = new DataOutputStream(new FileOutputStream(featureFile.getAbsolutePath()));

        // data saving
        for(int x=0 ; x<featureData.size() ; x++) {
            dos.writeDouble(featureData.get(x)); // write each short into the file
            dos.flush();
        }
    }

    /**
     * load converted pcm data(.dat) from "/data"
     * @return ArrayList<Double> - converted pcm data
     */
    // TODO: 3/14/2017
    public ArrayList<Double> loadData() {
        return null;
    }

    /**
     * load extracted feature data(.fea) from "/feature"
     * @return ArrayList<Double> - feature data
     */
    // TODO: 3/14/2017
    public ArrayList<Double> loadFeature() {
        return null;
    }
}