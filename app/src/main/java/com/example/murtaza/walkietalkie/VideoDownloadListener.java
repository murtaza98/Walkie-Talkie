package com.example.murtaza.walkietalkie;

public interface VideoDownloadListener {
    public void onVideoDownloaded();
    public void onVideoDownloadError(Exception e);
}
