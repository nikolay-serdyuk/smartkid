package com.nserdyuk.smartkid.tasks;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.nserdyuk.smartkid.R;
import com.nserdyuk.smartkid.io.TextReader;

import java.io.IOException;
import java.util.Arrays;

abstract class AbstractChatBot extends HandlerThread implements IChat {
    private static final String TAG = "AbstractChatBot";
    private static final String ERROR = "An error occurred in chat bot";
    private static final String CONTENT_ERROR = "Invalid content";

    private final String greetingMsg;

    private int currentTestNum;
    private int currentAnswer;
    private Handler mHandler;
    private Test[] tests;
    private int numberOfTests = 0;
    private TextReader textReader;
    private OnErrorListener onErrorListener;

    AbstractChatBot(Context context, AssetManager am, String fileName, int examples) {
        super(TAG);
        textReader = new TextReader(am, fileName, examples);
        greetingMsg = context.getResources().getString(R.string.greeting);
        currentTestNum = 0;
        currentAnswer = 0;
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
                    process((String) msg.obj);
                }
            }
        };
        startBot();
    }

    private void startBot() {
        process("");
    }

    private void process(String input) {
        try {
            String msg;
            if (input.isEmpty()) {
                send(greetingMsg);
                loadNextTests();
                msg = tests[currentTestNum].getQuestion();
            } else {
                tests[currentTestNum].setUserAnswer(currentAnswer++, input);
                if (currentAnswer < tests[currentTestNum].getNumberOfAnswers()) {
                    msg = "Eще";
                } else {
                    boolean result = tests[currentTestNum].checkAllAnswers();
                    msg = "";

                }
            }
            send(msg);
        } catch (ChatBotException e) {
            Log.e(TAG, ERROR, e);
            if (onErrorListener != null) {
                onErrorListener.onError(e.getMessage());
            }
        }
    }


    private void loadNextTests() throws ChatBotException {
        String[] lines;
        try {
            lines = textReader.readRandomLines();
        } catch (IOException e) {
            throw new ChatBotException(e.getMessage(), e);
        }

        tests = new Test[lines.length];
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String[] parts = line.split(Constants.STRING_DELIMITER);
            if (parts.length < 2) {
                throw new ChatBotException(CONTENT_ERROR);
            }
            tests[i] = new Test(parts[0], Arrays.copyOfRange(parts, 1, parts.length - 1));
        }
        currentTestNum = 0;
    }

    @NonNull
    private String reply(boolean loadNextQuestions) throws ChatBotException {
        if (loadNextQuestions) {
        }
        return tests[currentTestNum].getQuestion();
    }

    interface OnErrorListener {
        void onError(String message);
    }

    private class Test {
        private final String question;
        private final String[] rightAnswers;
        private final String[] userAnswers;

        Test(String question, String[] rightAnswers) {
            this.question = question;
            this.rightAnswers = rightAnswers;
            this.userAnswers = new String[rightAnswers.length];
        }

        String getQuestion() {
            return question;
        }

        void setUserAnswer(int num, String answer) {
            if (num < userAnswers.length) {
                userAnswers[num] = answer;
            }
        }

        int getNumberOfAnswers() {
            return rightAnswers.length;
        }

        boolean checkAllAnswers() {
            return Arrays.equals(rightAnswers, userAnswers);
        }
    }

    private class ChatBotException extends Exception {

        ChatBotException(String message) {
            super(message);
        }

        ChatBotException(String message, Throwable cause) {
            super(message, cause);
        }

    }
}
