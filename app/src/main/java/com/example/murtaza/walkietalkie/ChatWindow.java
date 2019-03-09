package com.example.murtaza.walkietalkie;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
    static final String file_name = "test.mp3";
    private static final int MESSAGE_READ = 1;
    SendReceive sendReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        send_btn = (Button)findViewById(R.id.send_file_btn);

        Socket socket = SocketHandler.getSocket();
        sendReceive = new SendReceive(socket);
        sendReceive.start();
    }

    private class SendReceive extends Thread{
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        SendReceive(Socket socket){
            this.socket = socket;
            try {
                this.inputStream = socket.getInputStream();
                this.outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes_len;

            while (socket != null){
                try {
                    bytes_len = inputStream.read(buffer);
//                    if(bytes_len > 0){
//                        handler.obtainMessage(MESSAGE_READ, bytes_len, -1, buffer).sendToTarget();
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] messageBytes){
            try {
                outputStream.write(messageBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void sendFile(){
        String internal_storage_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        String full_file_path = internal_storage_path + file_name;
        File file = new File(full_file_path);
        int size = (int) file.length();
        byte[] bytes = new byte[size];

        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();

            sendReceive.write(bytes);


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_file_btn:
                sendFile();
                break;
            default:
                break;
        }
    }
}
