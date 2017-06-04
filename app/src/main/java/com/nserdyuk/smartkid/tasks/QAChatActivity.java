package com.nserdyuk.smartkid.tasks;

import android.os.Bundle;

import com.nserdyuk.smartkid.tasks.base.AbstractChatActivity;

public class QAChatActivity extends AbstractChatActivity {
    private QAChatBot bot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bot = new QAChatBot() {
            @Override
            protected void send(String msg) {
                QAChatActivity.this.recieve(msg);
            }
        };
        bot.start();
    }

    protected void send(String msg) {
        bot.recieve(msg);
    }
}
