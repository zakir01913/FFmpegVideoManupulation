package com.zakir.ffmpegvideomanupulation;

import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.zakir.ffmpegvideomanupulation.model.VideoFile;
import com.zakir.ffmpegvideomanupulation.usecase.GetFrameFromVideo;
import com.zakir.ffmpegvideomanupulation.usecase.MergeVideos;
import com.zakir.ffmpegvideomanupulation.utils.AppStorageDirUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.observers.DisposableObserver;

public class MainActivityPresenter implements MainActivityContract.Presenter {
    private MainActivityContract.View view;
    private FFmpeg ffmpeg;
    private boolean isFFmpegLoaded = false;
    private List<VideoFile> videoFiles = new ArrayList<>();
    private String TAG = MainActivityPresenter.class.getName();
    private MergeVideos mergeVideos;
    private GetFrameFromVideo getFrameFromVideo;

    public MainActivityPresenter(MainActivityContract.View view) {
        this.view = view;
        ffmpeg = FFmpeg.getInstance(view.getViewContext());
        generateVideoFiles();
    }

    public void setMergeVideos(MergeVideos mergeVideos) {
        this.mergeVideos = mergeVideos;
    }

    public void setGetFrameFromVideo(GetFrameFromVideo getFrameFromVideo) {
        this.getFrameFromVideo = getFrameFromVideo;
    }

    @Override
    public void loadFFmpeg() {
        view.showProgress("Loading FFmpeg...");

        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                }

                @Override
                public void onFailure() {
                    isFFmpegLoaded = false;
                    view.hideProgress();
                    view.showMessage("Failed to FFmpeg");
                }

                @Override
                public void onSuccess() {
                    isFFmpegLoaded = true;
                    view.hideProgress();
                    view.showMessage("FFmpeg loading successful");
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mereVideos() {
        if (isFFmpegLoaded) {
            view.showProgress("Merging videos...");
            MergeVideos.Request request  = new MergeVideos.Request(videoFiles);
            mergeVideos.execute(new MergeVideoDisposableObserver(), request);
        } else {
            view.showMessage("FFmpeg isn't loaded");
        }
    }

    @Override
    public void extractFrameFromVideo() {
        if (isFFmpegLoaded) {
            view.showProgress("Extracting frame...");
            GetFrameFromVideo.Request request = new GetFrameFromVideo.Request(videoFiles.get(0),0, 0, 5);
            getFrameFromVideo.execute(new FrameExtractionObserver(), request);

        } else {
            view.showMessage("FFmpeg isn't loaded");
        }
    }


    private class MergeVideoDisposableObserver extends DisposableObserver<VideoFile> {

        @Override
        public void onNext(VideoFile videoFile) {
            Log.d(TAG, videoFile.getFilePath());
            view.hideProgress();
            view.showMergeView(videoFile);
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            mergeVideos.remove(this);
        }

        @Override
        public void onComplete() {
            mergeVideos.remove(this);
        }

    }

    private class FrameExtractionObserver extends DisposableObserver<String> {

        @Override
        public void onNext(String framePath) {
            Log.d(TAG, framePath);
            view.hideProgress();
            view.showExtractedVideoFrame(framePath);
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            getFrameFromVideo.remove(this);
        }

        @Override
        public void onComplete() {
            getFrameFromVideo.remove(this);
        }

    }

    private void generateVideoFiles() {
        for (String fileName : AppStorageDirUtils.fileNames) {
            videoFiles.add(new VideoFile(AppStorageDirUtils.APP_VIDEO_STORAGE_DIR + fileName));
        }
    }
}
