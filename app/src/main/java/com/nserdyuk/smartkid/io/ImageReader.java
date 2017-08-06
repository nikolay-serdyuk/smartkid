package com.nserdyuk.smartkid.io;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.nserdyuk.smartkid.common.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ImageReader extends AsyncTask<String, Void, Drawable> {
    private static final String ERROR_LOAD_IMAGES = "An error occurred while loading images";
    private static final String ERROR_NO_IMAGES_FOUND = "No images found with mask %s";
    private static final String TAG = ImageReader.class.getName();

    private final AssetManager assetManager;

    public ImageReader(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    @Override
    protected Drawable doInBackground(String... mask) {
        Drawable drawable = null;
        try (InputStream is = readRandomImage(assetManager, mask[0])){
            drawable = Drawable.createFromStream(is, null);
        } catch (IOException e) {
            Log.e(TAG, ERROR_LOAD_IMAGES, e);
        }
        return drawable;
    }

    private InputStream readRandomImage(AssetManager am, String mask) throws IOException {
        Utils.assertNonUiThread();
        List<String> list = getAvailableImages(am, mask);
        return am.open(list.get(new Random().nextInt(list.size())));
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
