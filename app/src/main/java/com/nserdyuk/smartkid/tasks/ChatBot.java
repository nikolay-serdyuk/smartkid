package com.nserdyuk.smartkid.tasks;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class ChatBot implements Runnable {
    private Handler mHandler;
    private Qa[] qaArray;
    private int questions = 0;
    private IChatListener listener;

    public ChatBot(IChatListener listener) {
        this.listener = listener;
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj instanceof String) {
                    String s = (String)msg.obj;
                    process(s);
                }
            }
        };
    }

    private void send(String msg) {
        if (listener != null) {
            listener.handleEvent(msg);
        }
    }

    public void receive(String msg) {
        Message m = mHandler.obtainMessage();
        m.obj = msg;
        m.sendToTarget();
    }

    @Override
    public void run() {
        Looper.prepare();
        receive("");
        Looper.loop();
    }

    public void process(String answer) {
        if (answer.isEmpty()) {
            question(true);
        } else {
            question(check(answer));
        }
        send("BYTTTTT");
    }

    private boolean check(String answer) {
        return true;
    }

    private void question(boolean loadNextQuestions) {
        if (loadNextQuestions) {

        }
    }

    private static class Qa {
        private String rightAnswer;
        private String userAnswer;

        public String getRightAnswer() {
            return rightAnswer;
        }

        public String getUserAnswer() {
            return userAnswer;
        }

        public void setRightAnswer(String rightAnswer) {
            this.rightAnswer = rightAnswer;
        }

        public void setUserAnswer(String userAnswer) {
            this.userAnswer = userAnswer;
        }
    }
}
