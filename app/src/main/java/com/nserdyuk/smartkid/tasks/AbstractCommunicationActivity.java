package com.nserdyuk.smartkid.tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

public abstract class AbstractCommunicationActivity extends AppCompatActivity implements ICommunication {
    private volatile Handler handler;

    @Override
    public void receive(Object object) {
        Message m = handler.obtainMessage();
        m.obj = object;
        m.sendToTarget();
    }

    public abstract void send(Object msg);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                process(msg.obj);
            }
        };
    }

    protected void process(Object o) {}

}
