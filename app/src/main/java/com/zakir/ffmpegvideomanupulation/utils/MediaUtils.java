package com.zakir.ffmpegvideomanupulation.utils;

import android.media.MediaMetadataRetriever;

import com.zakir.ffmpegvideomanupulation.model.VideoFile;

public class MediaUtils {
    private MediaUtils(){}

    public static VideoFile.Resolution getVideoResolution(String videoFilePath) {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(videoFilePath);
        String height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        return new VideoFile.Resolution(Integer.parseInt(width), Integer.parseInt(height));
    }
}
