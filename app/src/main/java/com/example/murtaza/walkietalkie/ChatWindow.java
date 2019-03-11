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
    static final String file_name = "/test.mp3";
    private static final int MESSAGE_READ = 1;
    SendReceive sendReceive;
    OutputStream outputStream;

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
        } catch (IOException e) {
            e.printStackTrace();
        }

//        sendReceive = new SendReceive(socket);
//        sendReceive.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menu_inflater = getMenuInflater();
        menu_inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.stream_btn) {
            startActivity(new Intent(getApplicationContext(), StreamingActivity.class));
        }

        return super.onOptionsItemSelected(item);
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
                    if(bytes_len > 0){
//                        handler.obtainMessage(MESSAGE_READ, bytes_len, -1, buffer).sendToTarget();
                        Log.e("FILE_PATH", "Data transfered "+bytes_len);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] messageBytes){
            try {
                outputStream.write(messageBytes);
                Log.e("FILE_PATH", "Write on Output stream complete");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void sendFile(){
        String internal_storage_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        String full_file_path = internal_storage_path + file_name;
//        Toast.makeText(getApplicationContext(), full_file_path, Toast.LENGTH_SHORT).show();
        Log.e("FILE_PATH", full_file_path);
        File file = new File(full_file_path);
        int size = (int) file.length();
        final byte[] bytes = new byte[size];

        try {
            Log.e("FILE_READ", "File read start");
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();

            Log.e("FILE_READ", "File read complete");

//            sendReceive.write(bytes);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        outputStream.write(bytes);
                        Log.e("FILE_READ", "Output stream write complete");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            Log.e("FILE_READ", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e("FILE_READ", e.getMessage());
            e.printStackTrace();
        } catch (Exception e){
            Log.e("FILE_READ", e.getMessage());
            e.printStackTrace();
        }



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_file_btn:
                Toast.makeText(getApplicationContext(), "Button Clicked", Toast.LENGTH_SHORT).show();
                sendFile();
                break;
            default:
                break;
        }
    }
}
