package com.nserdyuk.smartkid.io;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.nserdyuk.smartkid.common.Utils;
import com.nserdyuk.smartkid.common.ErrorListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ImageReader extends AsyncTask<String, Void, Drawable> {
    private static final String ERROR_LOAD_IMAGES = "An error occurred while loading images";
    private static final String ERROR_NO_IMAGES_FOUND = "No images found with mask %s";
    private static final String TAG = ImageReader.class.getName();

    private final AssetManager assetManager;
    private volatile ErrorListener errorListener;

    public ImageReader(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public void setOnErrorListener(ErrorListener listener) {
        errorListener = listener;
    }

    @Nullable
    @Override
    protected Drawable doInBackground(String... mask) {
        Drawable drawable = null;
        try (InputStream is = readRandomAsset(assetManager, mask[0])){
            drawable = Drawable.createFromStream(is, null);
        } catch (IOException e) {
            Log.e(TAG, ERROR_LOAD_IMAGES, e);
            if (errorListener != null) {
                errorListener.onError(e);
            }
        }
        return drawable;
    }

    private InputStream readRandomAsset(AssetManager am, String mask) throws IOException {
        Utils.assertNonUiThread();
        List<String> list = Utils.getFilteredAssetsList(am, mask);
        if (list.isEmpty()) {
            throw new IOException(String.format(Locale.US, ERROR_NO_IMAGES_FOUND, mask));
        }
        return am.open(list.get(new Random().nextInt(list.size())));
    }
}
