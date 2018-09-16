package com.zakir.ffmpegvideomanupulation.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class FileUtils {
    private FileUtils() {
    }

    public static void moveAssetFileToExternalStorageDir(Context context, String assetFilePath, File outputFile) throws IOException {
        InputStream is = context.getAssets().open(assetFilePath);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();

        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(buffer);
        fos.close();
    }

    public static void createDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
