package com.zakir.ffmpegvideomanupulation.usecase;

import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.zakir.ffmpegvideomanupulation.utils.AppStorageDirUtils;
import com.zakir.ffmpegvideomanupulation.model.VideoFile;
import com.zakir.ffmpegvideomanupulation.utils.FFmpegCommandUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Scheduler;

public class GetFrameFromVideo extends BaseUseCase<String, GetFrameFromVideo.Request, Exception> {

    private final FFmpeg fFmpeg;
    private static String TAG = GetFrameFromVideo.class.getName();
    String framePath = AppStorageDirUtils.APP_VIDEO_FRAME_STORAGE_DIR + "frame" + System.currentTimeMillis() + ".jpg";

    public GetFrameFromVideo(FFmpeg ffmpeg, Scheduler subscriberScheduler, Scheduler observerScheduler) {
        super(subscriberScheduler, observerScheduler);
        this.fFmpeg = ffmpeg;
    }

    @Override
    Observable<String> build(Request request) {
        return Observable.create(emitter -> {
            if (request.videoFile == null) {
                emitter.onError(new Exception("Video file is null"));
            }
            fFmpeg.execute(FFmpegCommandUtils.buildFrameExtractionCommand(request.videoFile, request.hour, request.minute, request.second, framePath),
                    new ExecuteBinaryResponseHandlerImp(emitter));
        });
    }

    public static final class Request {
        VideoFile videoFile;
        int hour, minute, second;

        public Request(VideoFile videoFile, int hour, int minute, int second) {
            this.videoFile = videoFile;
            this.hour = hour;
            this.minute = minute;
            this.second = second;
        }
    }

    private final class ExecuteBinaryResponseHandlerImp extends ExecuteBinaryResponseHandler {
        ObservableEmitter<String> emitter;

        public ExecuteBinaryResponseHandlerImp(ObservableEmitter<String> emitter) {
            this.emitter = emitter;
        }

        @Override
        public void onStart() {
            Log.d(TAG, "onStart");
        }

        @Override
        public void onProgress(String message) {
            Log.d(TAG, "onProgress - " + message);

        }

        @Override
        public void onFailure(String message) {
            Log.d(TAG, "onFailure - " + message);
            Exception exception = new Exception("Failed to extract frame from video");
            emitter.onError(exception);
        }

        @Override
        public void onSuccess(String message) {
            Log.d(TAG, "onSuccess - " + message);
            emitter.onNext(framePath);
        }

        @Override
        public void onFinish() {
            Log.d(TAG, "onFinish");
            emitter.onComplete();
            emitter = null;
        }
    }
}
