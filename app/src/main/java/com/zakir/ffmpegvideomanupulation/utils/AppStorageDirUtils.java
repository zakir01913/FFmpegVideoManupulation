package com.zakir.ffmpegvideomanupulation.utils;

import android.content.Context;
import android.os.Environment;

import com.zakir.ffmpegvideomanupulation.utils.FileUtils;

import java.io.File;
import java.io.IOException;

public final class AppStorageDirUtils {

    public static final String APP_STORAGE_ROOT_DIR = Environment.getExternalStorageDirectory() + "/FFmpegVideoManupulation/";
    public static final String APP_VIDEO_STORAGE_DIR = APP_STORAGE_ROOT_DIR + "6";
    public static final String APP_VIDEO_FRAME_STORAGE_DIR = APP_STORAGE_ROOT_DIR + "Frame/";

    public final static String[] fileNames = {
            "63.mp4",
            "74.mp4"
    };

    private AppStorageDirUtils() {
    }

    public static void createAppStorageDir(){
        FileUtils.createDir(APP_VIDEO_STORAGE_DIR);
        FileUtils.createDir(APP_VIDEO_FRAME_STORAGE_DIR);
    }

    public static void moveAssetVideoFilesToAppExternalDir(Context context) {
        for (String fileName : fileNames) {
            moveFileIfNotExist(context, fileName, AppStorageDirUtils.APP_VIDEO_STORAGE_DIR + fileName);
        }
    }

    private static void moveFileIfNotExist(Context context, String fileName, String outputFilePath) {
        File file = new File(outputFilePath);
        if (!file.exists()) {
            try {
                FileUtils.moveAssetFileToExternalStorageDir(context, fileName, file );
            } catch (IOException e) {
            }
        }
    }

}
