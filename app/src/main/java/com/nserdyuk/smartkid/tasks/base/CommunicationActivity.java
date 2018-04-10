package com.nserdyuk.smartkid.tasks.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

public abstract class CommunicationActivity extends AppCompatActivity {
    private volatile Handler handler;

    protected void receive(Object object) {
        Message m = handler.obtainMessage();
        m.obj = object;
        m.sendToTarget();
    }

    protected abstract void send(Object msg);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                onMessage(msg.obj);
            }
        };
    }

    protected abstract void onMessage(Object o);
}
