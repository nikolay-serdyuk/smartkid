package com.nserdyuk.smartkid.common;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Looper;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Utils {
    private static final String ERROR_CANT_RUN_IN_UI = "can't run in UI thread";

    private Utils() {
    }

    public static List<String> getFilteredAssetsList(AssetManager am, String path, String mask)
            throws IOException {
        return Optional.ofNullable(am.list(path))
                .map(Arrays::stream)
                .orElseGet(Stream::empty)
                .filter(file -> file.contains(mask))
                .map(f -> path + File.separator + f)
                .collect(Collectors.toList());
    }

    public static void assertNonUiThread() {
        if (isUiThread()) {
            throw new AssertionError(ERROR_CANT_RUN_IN_UI);
        }
    }

    public static void showError(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        activity.finish();
    }

    public static void showErrorInUiThread(Activity activity, String message) {
        activity.runOnUiThread(new ErrorReporter(activity, message));

    }

    public static void delay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static boolean isUiThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    private static class ErrorReporter implements Runnable {
        private final Activity a;
        private final String m;

        ErrorReporter(Activity a, String m) {
            this.a = a;
            this.m = m;
        }

        @Override
        public void run() {
            showError(a, m);
        }
    }
}
