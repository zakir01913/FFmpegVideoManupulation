package com.zakir.ffmpegvideomanupulation.usecase;

import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.zakir.ffmpegvideomanupulation.utils.AppStorageDirUtils;
import com.zakir.ffmpegvideomanupulation.exception.MergeVideosException;
import com.zakir.ffmpegvideomanupulation.model.VideoFile;
import com.zakir.ffmpegvideomanupulation.utils.FFmpegCommandUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Scheduler;

public class MergeVideos extends BaseUseCase<VideoFile, MergeVideos.Request, MergeVideosException>{

    private final FFmpeg fFmpeg;
    private static String TAG = MergeVideos.class.getName();
    String mergeVideoPath = AppStorageDirUtils.APP_VIDEO_STORAGE_DIR + "video" + System.currentTimeMillis() + ".mp4";

    public MergeVideos(FFmpeg ffmpeg, Scheduler subscriberScheduler, Scheduler observerScheduler) {
        super(subscriberScheduler, observerScheduler);
        this.fFmpeg = ffmpeg;
    }

    @Override
    Observable<VideoFile> build(Request request) {
        return Observable.create(emitter -> {
            if (request.videoFiles.size() < 2) {
                emitter.onError(new MergeVideosException("You have to provide at least two video files"));
                return;
            }
            fFmpeg.execute(FFmpegCommandUtils.buildMergeCommand(request.videoFiles, mergeVideoPath),
                    new ExecuteBinaryResponseHandlerImp(emitter));
        });
    }

    public static final class Request {
        final List<VideoFile> videoFiles;

        public Request(List<VideoFile> videoFiles) {
            this.videoFiles = videoFiles;
        }
    }

   private final class ExecuteBinaryResponseHandlerImp extends ExecuteBinaryResponseHandler {
        ObservableEmitter<VideoFile> emitter;

        public ExecuteBinaryResponseHandlerImp(ObservableEmitter<VideoFile> emitter) {
            this.emitter = emitter;
        }

        @Override
        public void onStart()
        {
            Log.d(TAG,"onStart");
        }

        @Override
        public void onProgress(String message)
        {
            //Log.d(TAG,"onProgress - "+message);

        }

        @Override
        public void onFailure(String message)
        {
            Log.d(TAG,"onFailure - "+message);
            MergeVideosException mergeVideosException = new MergeVideosException("Video merge failed");
            emitter.onError(mergeVideosException);
        }

        @Override
        public void onSuccess(String message)
        {
            Log.d(TAG,"onSuccess - "+message);
            try {
                VideoFile videoFile = new VideoFile(mergeVideoPath);
                emitter.onNext(videoFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFinish()
        {
            Log.d(TAG,"onFinish");
            emitter.onComplete();
            emitter = null;
        }
    }

}
