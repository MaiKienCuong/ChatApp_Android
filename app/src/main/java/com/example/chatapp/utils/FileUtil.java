package com.example.chatapp.utils;

import com.example.chatapp.enumvalue.MediaType;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.List;

public class FileUtil {
    private static final List<String> images = List.of("jpg", "jpeg", "png", "gif", "bmp");
    private static final List<String> videos = List.of(
            "mp4", //MP4
            "mpg", "mp2", "mpeg", "mpeg", "mpv", // MPEG-1
            "m2v", // MPEG-2
            "3gp", "3g2" // 3gp
    );

    public static MediaType getMessageType(File file) {
        if (images.contains(FilenameUtils.getExtension(file.getName()).toLowerCase())) {
            return MediaType.IMAGE;
        } else if (videos.contains(FilenameUtils.getExtension(file.getName()).toLowerCase())) {
            return MediaType.VIDEO;
        } else {
            return MediaType.FILE;
        }
    }

    public static MediaType getMessageType(String url) {
        if (images.contains(FilenameUtils.getExtension(url).toLowerCase())) {
            return MediaType.IMAGE;
        } else if (videos.contains(FilenameUtils.getExtension(url).toLowerCase())) {
            return MediaType.VIDEO;
        } else {
            return MediaType.FILE;
        }
    }
}
