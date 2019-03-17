package com.example.murtaza.walkietalkie;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

public class MicRecorder implements Runnable {
    private static final int SAMPLE_RATE = 16000;
    public static volatile boolean keepRecording = true;

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        Log.e("AUDIO", "buffersize = "+bufferSize);

        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2;
        }

        try {
            final OutputStream outputStream = SocketHandler.getSocket().getOutputStream();

            final byte[] audioBuffer = new byte[bufferSize];

            AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize);

            if (record.getState() != AudioRecord.STATE_INITIALIZED) {
                Log.e("AUDIO", "Audio Record can't initialize!");
                return;
            }
            record.startRecording();
            Log.e("AUDIO", "STARTED RECORDING");

            while(keepRecording) {
                int numberOfBytes = record.read(audioBuffer, 0, audioBuffer.length);
                Runnable writeToOutputStream = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            outputStream.write(audioBuffer);
                            outputStream.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                Thread thread = new Thread(writeToOutputStream);
                thread.start();
            }

            record.stop();
            record.release();
//            outputStream.close();
            Log.e("AUDIO", "Streaming stopped");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
