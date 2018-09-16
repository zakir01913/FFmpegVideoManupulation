package com.zakir.ffmpegvideomanupulation;

import android.content.Context;

import com.zakir.ffmpegvideomanupulation.model.VideoFile;

public interface MainActivityContract {

    interface View {
        void showProgress(String message);
        void hideProgress();
        Context getViewContext();
        void showMessage(String message);
        void showMergeView(VideoFile videoFile);
        void showExtractedVideoFrame(String framePath);
    }

    interface Presenter {
        void loadFFmpeg();
        void mereVideos();
        void extractFrameFromVideo();
    }
}
