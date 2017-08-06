package com.nserdyuk.smartkid.io;

import android.os.Looper;

public final class Utils {
    private static String ERROR_CANT_RUN_IN_UI = "can't run in UI thread";

    public static void assertNonUiThread() {
        if (isUiThread()) {
            throw new AssertionError(ERROR_CANT_RUN_IN_UI);
        }
    }

    private static boolean isUiThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    private Utils() {}
}
