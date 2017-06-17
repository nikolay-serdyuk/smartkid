package com.nserdyuk.smartkid.io;

import android.content.res.AssetManager;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ImageLoader {
    public List<String> loadImagesAndShuffle(AssetManager am, String mask) throws IOException {
        List<String> images = new LinkedList<>();
        String[] list = am.list("");
        if (list.length > 0) {
            for (String file : list) {
                if (file.contains(mask)) {
                    images.add(file);
                }
            }
            Collections.shuffle(images, new Random(System.nanoTime()));
        }
        return images;
    }
}
