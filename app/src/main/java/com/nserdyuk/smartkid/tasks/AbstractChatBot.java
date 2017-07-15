package com.nserdyuk.smartkid.tasks;

import android.content.res.AssetManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.nserdyuk.smartkid.io.RandomReader;

import java.io.IOException;

abstract class AbstractChatBot extends HandlerThread implements IChat {
    private static final String TAG = "AbstractChatBot";
    private static final String ERROR = "An error occurred while processing user input";

    private Handler mHandler;
    private Qa[] qaArray;
    private int questions = 0;
    private RandomReader randomReader;
    private OnErrorListener onErrorListener;

    AbstractChatBot(AssetManager am, String fileName, int examples) {
        super(TAG);
        this.randomReader = new RandomReader(am, fileName, examples);
    }

    @Override
    public void receive(String msg) {
        Message m = mHandler.obtainMessage();
        m.obj = msg;
        m.sendToTarget();
    }

    public abstract void send(String msg);

    void setOnErrorListener(OnErrorListener listener) {
        onErrorListener = listener;
    }

    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj instanceof String) {
                    String output;
                    try {
                        output = process((String)msg.obj);
                    } catch (IOException e) {
                        Log.e(TAG, ERROR, e);
                        if (onErrorListener != null) {
                            onErrorListener.onError(ERROR);
                        }
                        return;
                    }
                    send(output);
                }
            }
        };
    }

    private String process(String input) throws IOException {
        boolean loadNextQuestions = input.isEmpty() || (check(input) ? true : false);
        return question(loadNextQuestions);
    }

    private boolean check(String answer) {
        return true;
    }

    private String question(boolean loadNextQuestions) throws IOException {
        if (loadNextQuestions) {
            String[] qaArray = randomReader.loadRandomLines();
        }
        return "";
    }

    interface OnErrorListener {
        void onError(String message);
    }

    private class Qa {
        private final String question;
        private final String[] rightAnswers;
        private final String[] userAnswers;

        public Qa(String question, String[] rightAnswers) {
            this.question = question;
            this.rightAnswers = rightAnswers;
            this.userAnswers = new String[rightAnswers.length];
        }

        public void setUserAnswer(int num, String answer) {
            if (num <= userAnswers.length) {
                userAnswers[num] = answer;
            }
        }

        public boolean checkAnswers() {
            return false;
        }
    }
}
