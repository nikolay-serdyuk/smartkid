package com.nserdyuk.smartkid.io;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.nserdyuk.smartkid.common.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public final class ImageReader {
    private static final String ERROR_NO_IMAGES_FOUND = "No images found with mask %s";
    private static final String ERROR_LOAD_IMAGES = "An error occurred while loading images";

    public static void setBackgroundRandomImage(final Activity activity, final ImageView imageView, final String mask) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    InputStream is = readRandomImage(activity.getAssets(), mask);
                    final Drawable drawable = Drawable.createFromStream(is, null);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageDrawable(drawable);
                        }
                    });
                } catch (IOException e) {
                    Log.e(activity.getClass().getName(), ERROR_LOAD_IMAGES, e);
                    Utils.showErrorInUiThread(activity, ERROR_LOAD_IMAGES);
                }
            }
        }).start();
    }

    private static InputStream readRandomImage(AssetManager am, String mask) throws IOException {
        Utils.assertNonUiThread();
        List<String> list = getAvailableImages(am, mask);
        Collections.shuffle(list);
        return am.open(list.get(0));
    }

    private static List<String> getAvailableImages(AssetManager am, String mask) throws IOException {
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
