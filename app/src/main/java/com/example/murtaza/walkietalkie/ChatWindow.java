package com.example.murtaza.walkietalkie;

import android.animation.TimeAnimator;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class ChatWindow extends AppCompatActivity implements View.OnClickListener {

    Button send_btn;
    private static final int MESSAGE_READ = 1;
    private static boolean isRecording = false;
    private MicRecorder micRecorder;
    OutputStream outputStream;
    Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        send_btn = (Button)findViewById(R.id.send_file_btn);
        send_btn.setOnClickListener(this);

        Socket socket = SocketHandler.getSocket();

        try {
            outputStream = socket.getOutputStream();
            Log.e("OUTPUT_SOCKET", "SUCCESS");
            startService(new Intent(getApplicationContext(), AudioStreamingService.class));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_file_btn:
                if(send_btn.getText().toString().equals("TALK")){
                    // stream audio
                    send_btn.setText("OVER");
                    micRecorder = new MicRecorder();
                    t = new Thread(micRecorder);
                    if(micRecorder != null) {
                        MicRecorder.keepRecording = true;
                    }
                    t.start();
                }else if(send_btn.getText().toString().equals("OVER")){
                    send_btn.setText("TALK");
                    if(micRecorder != null) {
                        MicRecorder.keepRecording = false;
                    }
                }

                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(micRecorder != null) {
            MicRecorder.keepRecording = false;
        }
    }
}
