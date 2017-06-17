package com.nserdyuk.smartkid.tasks;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public abstract class QAChatBot implements Runnable {
    private static String TAG = "QAChatBot";
    private Handler mHandler;

    public QAChatBot() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj instanceof String) {
                    String s = (String)msg.obj;
                    process(s);
                }
            }
        };

        // 1. Попробовать Looper.loop() вместо SynchronousQueue https://developer.android.com/reference/android/os/Looper.html
        // 2. Вместо abstract сделать класс EventListener { void notify(String msg) }
        // 3. сделать от Runable а не от Thread
    }

    public void receive(String msg) {
        Message m = mHandler.obtainMessage();
        m.obj = msg;
        m.sendToTarget();
    }

    @Override
    public void run() {
        Looper.prepare();
        Looper.loop();
    }

    protected abstract void send(String msg);

    private void process(String msg) {
        //
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        msg = "Hello";
        Log.d("CHAT", msg);
        //
        send(msg);
    }
}
