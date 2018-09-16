package com.zakir.ffmpegvideomanupulation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.constraint.Group;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.zakir.ffmpegvideomanupulation.model.VideoFile;
import com.zakir.ffmpegvideomanupulation.usecase.GetFrameFromVideo;
import com.zakir.ffmpegvideomanupulation.usecase.MergeVideos;
import com.zakir.ffmpegvideomanupulation.utils.AppStorageDirUtils;

import java.io.File;
import java.util.function.Function;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements MainActivityContract.View {

    private String TAG = MainActivity.class.getName();
    MainActivityContract.Presenter presenter;

    @BindView(R.id.merge_video_path_tv)
    TextView mergeVideoPathTextView;
    @BindView(R.id.pb)
    ProgressBar progressBar;
    @BindView(R.id.pb_title_tv)
    TextView pb_title_tv;
    @BindView(R.id.progress_group)
    Group progressGroup;
    @BindView(R.id.frame_iv)
    ImageView frameImageView;
    @BindView(R.id.merge_video_group)
    Group mergeVideoGroup;
    @BindView(R.id.merge_vv)
    VideoView mergeVideoView;
    @BindView(R.id.frame_path_tv)
    TextView frame_path_tv;
    @BindView(R.id.frame_extraction_group)
    Group frameExtractionGroup;

    private  final int EXTERNAL_STORAGE_PERMISSION_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (hasExternalStorageWritePermission()) {
            init();
        } else {
            requestPermission();
        }

    }

    private void init() {
        AppStorageDirUtils.createAppStorageDir();
        AppStorageDirUtils.moveAssetVideoFilesToAppExternalDir(this);

        MainActivityPresenter mainActivityPresenter = new MainActivityPresenter(this);
        FFmpeg fFmpeg = FFmpeg.getInstance(this);

        MergeVideos mergeVideos = new MergeVideos(fFmpeg, Schedulers.computation(), AndroidSchedulers.mainThread());
        mainActivityPresenter.setMergeVideos(mergeVideos);

        GetFrameFromVideo getFrameFromVideo = new GetFrameFromVideo(fFmpeg, Schedulers.computation(), AndroidSchedulers.mainThread());
        mainActivityPresenter.setGetFrameFromVideo(getFrameFromVideo);

        mainActivityPresenter.loadFFmpeg();
        presenter = mainActivityPresenter;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                }
                return;
            }
        }
    }

    @Override
    public void showProgress(String message) {
        if (message == null) {
            pb_title_tv.setText("");
        } else {
            pb_title_tv.setText(message);
        }
        progressGroup.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressGroup.setVisibility(View.GONE);
    }

    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMergeView(VideoFile videoFile) {
        frameExtractionGroup.setVisibility(View.GONE);

        mergeVideoGroup.setVisibility(View.VISIBLE);
        mergeVideoPathTextView.setText(videoFile.getFilePath());
        mergeVideoView.setVideoPath(videoFile.getFilePath());
        mergeVideoView.start();
    }

    @Override
    public void showExtractedVideoFrame(String framePath) {
        mergeVideoGroup.setVisibility(View.GONE);

        File image = new File(framePath);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);

        frame_path_tv.setText(framePath);
        frameImageView.setImageBitmap(bitmap);
        frameExtractionGroup.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.merge_video_btn)
    public void mergeVideoButtonClick() {
        if (hasExternalStorageWritePermission()) {
            presenter.mereVideos();
        } else {
            requestPermission();
        }
    }

    @OnClick(R.id.extract_frame_btn)
    public void extractFrameButtonClick() {
        if (hasExternalStorageWritePermission()) {
            presenter.extractFrameFromVideo();
        } else {
            requestPermission();
        }
    }

    private boolean hasExternalStorageWritePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                EXTERNAL_STORAGE_PERMISSION_REQUEST);
    }
}
