package com.zakir.ffmpegvideomanupulation.model;

import com.zakir.ffmpegvideomanupulation.utils.MediaUtils;

public class VideoFile {

    private final String filePath;
    private Resolution resolution;

    public VideoFile(String filePath){
        this.filePath = filePath;
        resolution = MediaUtils.getVideoResolution(filePath);
    }

    public String getFilePath() {
        return filePath;
    }

    public Resolution getResolution() {
        return resolution;
    }

    public static final class Resolution {
        private final int width;
        private final int height;

        public Resolution(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Resolution))
                return false;

            Resolution resolution = (Resolution) obj;
            if (width == resolution.width && height == resolution.height)
                return true;

            return false;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }
}
