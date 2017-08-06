package com.nserdyuk.smartkid.io;

import android.content.res.AssetManager;

import com.nserdyuk.smartkid.common.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class ImageReader {
    private static final String ERROR_NO_IMAGES_FOUND = "No images found with mask %s";
    public InputStream readRandomImage(AssetManager am, String mask) throws IOException {
        Utils.assertNonUiThread();
        List<String> list = getAvailableImages(am, mask);
        Collections.shuffle(list);
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
        if (images.size() == 0) {
            throw new IOException(String.format(Locale.US, ERROR_NO_IMAGES_FOUND, mask));
        }
        return images;
    }
}
