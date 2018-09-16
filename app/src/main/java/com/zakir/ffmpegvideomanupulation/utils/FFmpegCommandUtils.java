package com.zakir.ffmpegvideomanupulation.utils;

import android.util.Log;

import com.zakir.ffmpegvideomanupulation.model.VideoFile;

import java.util.List;

public final class FFmpegCommandUtils {

    private static final String COMMAND_SEPARATOR = "#";
    private static final String TAG = FFmpegCommandUtils.class.getName();
    private static String mergeVideoCmd = "-yinput_files#-filter_complex#filter_complex_config#-ab#48000#-ac#2#-ar#22050#-s#merge_video_resolution#-vcodec#libx264#-crf#27#-preset#ultrafast#ouput_path";
    private static String frameExtractionCmd = "-i#input_file#-ss#frame_time#-vframes#1#output_path";

    private FFmpegCommandUtils() {
    }



    public static String[] buildMergeCommand(List<VideoFile> videoFiles, String outputFilePath) {
        // For video merging all files should have same resolution otherwise videos need to be scaled
        // For simplicity I choose the file's resolution as a desire resolution for merge video
        VideoFile.Resolution scaledResolution = videoFiles.get(0).getResolution();
        String scale = "" + scaledResolution.getWidth() + "x" + scaledResolution.getHeight();

        StringBuilder inputFiles = new StringBuilder();
        StringBuilder filterComplexScaleConfig = new StringBuilder();
        StringBuilder filterComplexAudioVideoConfig = new StringBuilder();

        for (int i = 0; i < videoFiles.size(); i++){
            VideoFile videoFile = videoFiles.get(i);
            inputFiles.append(COMMAND_SEPARATOR +"-i"+COMMAND_SEPARATOR + videoFile.getFilePath());
            filterComplexScaleConfig.append("["+i+":v]scale="+scale+",setsar=1[v"+i+"];");
            filterComplexAudioVideoConfig.append("[v"+i+"]["+i+":a]");
        }
        String filterComplex = filterComplexScaleConfig.toString()
                + filterComplexAudioVideoConfig.append("concat=n="+videoFiles.size()+":v=1:a=1").toString();
        mergeVideoCmd = mergeVideoCmd.replace("input_files",inputFiles.toString());
        mergeVideoCmd = mergeVideoCmd.replace("filter_complex_config", filterComplex);
        mergeVideoCmd = mergeVideoCmd.replace("merge_video_resolution", scale);
        mergeVideoCmd = mergeVideoCmd.replace("ouput_path", outputFilePath);
        Log.d(TAG, mergeVideoCmd);
        return mergeVideoCmd.split(COMMAND_SEPARATOR);
    }

    public static String[] buildFrameExtractionCommand(VideoFile videoFile, int hour, int minute, int second, String outputFilePath) {
        frameExtractionCmd = frameExtractionCmd.replace("input_file", videoFile.getFilePath());
        frameExtractionCmd = frameExtractionCmd.replace("frame_time", "" + hour + ":" + minute + ":" + second);
        frameExtractionCmd = frameExtractionCmd.replace("output_path", outputFilePath);
        return frameExtractionCmd.split(COMMAND_SEPARATOR);
    }
}
