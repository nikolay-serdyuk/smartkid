package com.nserdyuk.smartkid.tasks.base;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.nserdyuk.smartkid.common.ErrorListener;

public abstract class Bot extends HandlerThread {
    private volatile Handler handler;
    private volatile ErrorListener errorListener;


    public Bot(String name) {
        super(name);
    }

    public void receive(Object msg) {
        Message m = handler.obtainMessage();
        m.obj = msg;
        m.sendToTarget();
    }

    public void setOnErrorListener(ErrorListener listener) {
        errorListener = listener;
    }

    protected abstract void send(Object msg);

    @Override
    protected void onLooperPrepared() {
        handler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                onMessage(msg.obj);
            }
        };
        onStart();
    }

    protected void onError(Exception e) {
        if (errorListener != null) {
            errorListener.onError(e);
        }
    }

    protected void onStart() {}

    protected abstract void onMessage(Object o);
}
