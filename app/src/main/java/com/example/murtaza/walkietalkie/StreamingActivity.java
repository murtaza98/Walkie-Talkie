package com.example.murtaza.walkietalkie;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;

public class StreamingActivity extends Activity implements SurfaceHolder.Callback {

    MediaPlayer mp;
    boolean isPaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming);
        SurfaceView surfaceView = (SurfaceView)findViewById(R.id.surface);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(this);
        mp = new MediaPlayer();
        isPaused = false;

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mp.setDisplay(holder);
            if(!isPaused) {
                mp.setDataSource("https://sample-videos.com/video123/mp4/240/big_buck_bunny_240p_30mb.mp4");
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
                mp.prepareAsync();
            }else {
                mp.start();
            }
            isPaused = false;
        } catch (IOException io) {

        } catch (IllegalStateException is){

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mp.isPlaying() && !isPaused) {
            mp.pause();
            isPaused = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mp != null) {
            mp.stop();
            mp.release();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}