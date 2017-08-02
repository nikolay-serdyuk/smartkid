package com.nserdyuk.smartkid.tasks;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

public abstract class AbstractBot extends HandlerThread implements IChat {
    private volatile Handler mHandler;

    public AbstractBot(String name) {
        super(name);
    }

    @Override
    public void receive(Object msg) {
        Message m = mHandler.obtainMessage();
        m.obj = msg;
        m.sendToTarget();
    }

    public abstract void send(Object msg);

    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                process(msg.obj);
            }
        };
        startBot();
    }

    protected void process(Object o) {}

    protected void startBot() {}
}
