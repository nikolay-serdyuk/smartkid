package com.nserdyuk.smartkid.tasks;

import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public abstract class QAChatBot extends Thread {
    private static String TAG = "QAChatBot";

    private BlockingQueue<String> incoming;

    public QAChatBot() {
        super();
        incoming = new SynchronousQueue<>();
        //
        send("HIIII");
        // 1. Попробовать Looper.loop() вместо SynchronousQueue https://developer.android.com/reference/android/os/Looper.html
        // 2. Вместо abstract сделать класс EventListener { void notify(String msg) }
        // 3. сделать от Runable а не от Thread
    }

    public void recieve(String msg) {
        if (isAlive()) {
            try {
                incoming.put(msg);
            } catch (InterruptedException e) {
                Log.e(TAG, "An exception occurred while putting message in queue", e);
                interrupt();
            }
        }
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            String msg;
            try {
                msg = incoming.take();
            } catch (InterruptedException e) {
                Log.e(TAG, "An exception occurred while getting message from queue", e);
                interrupt();
                break;
            }
            process(msg);
        }
    }

    protected abstract void send(String msg);

    private void process(String msg) {
        //
        try {
            sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        msg = "Hello";
        Log.d("BOT", msg);
        //
        send(msg);
    }
}
