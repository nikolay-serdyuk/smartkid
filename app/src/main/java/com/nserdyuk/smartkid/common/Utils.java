package com.nserdyuk.smartkid.common;

import android.app.Activity;
import android.os.Looper;
import android.widget.Toast;

public final class Utils {
    private static final String ERROR_CANT_RUN_IN_UI = "can't run in UI thread";

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

    private Utils() {}
}
