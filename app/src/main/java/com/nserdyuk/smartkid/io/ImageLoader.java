package com.nserdyuk.smartkid.io;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ImageLoader {
    public InputStream getRandomImage(AssetManager am, String mask) throws IOException {
        List<String> list = getAvailableImages(am, mask);
        Collections.shuffle(list, new Random(System.nanoTime()));
        return am.open(list.get(0));
    }

    private List<String> getAvailableImages(AssetManager am, String mask) throws IOException {
        List<String> images = new LinkedList<>();
        String[] list = am.list("");
        if (list.length > 0) {
            for (String file : list) {
                if (file.contains(mask)) {
                    images.add(file);
                }
            }
        }
        return images;
    }
}
