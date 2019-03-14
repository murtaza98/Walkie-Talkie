package com.example.murtaza.walkietalkie;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

public class AudioStreamingService extends Service implements MediaPlayer.OnPreparedListener {
    MediaPlayer audioPlayer;

    public AudioStreamingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        audioPlayer.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        audioPlayer = new MediaPlayer();
        VideoDataSource videoDataSource = new VideoDataSource();
        videoDataSource.downloadVideo(new VideoDownloadListener() {
            @Override
            public void onVideoDownloaded() {
                audioPlayer.prepareAsync();
            }

            @Override
            public void onVideoDownloadError(Exception e) {
                Log.d("STREAM", "error:"+e.toString());
            }
        });
        audioPlayer.setDataSource(videoDataSource);
        audioPlayer.setOnPreparedListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(audioPlayer != null) {
            audioPlayer.stop();
            audioPlayer.release();
        }
    }
}
