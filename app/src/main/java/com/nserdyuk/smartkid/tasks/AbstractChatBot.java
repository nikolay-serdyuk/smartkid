package com.nserdyuk.smartkid.tasks;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.nserdyuk.smartkid.R;
import com.nserdyuk.smartkid.common.Constants;
import com.nserdyuk.smartkid.io.TextReader;

import java.io.IOException;
import java.util.Arrays;

abstract class AbstractChatBot extends HandlerThread implements IChat {
    private static final String TAG = "AbstractChatBot";
    private static final String ERROR = "An error occurred in chat bot";
    private static final String CONTENT_ERROR = "Invalid content";

    private final String greetingMsg;
    private final String rightAnswerMsg;
    private final String wrongAnswerMsg;
    private final String nextAnswerMsg;

    private int examplesNum;
    private int currentExample;
    private int currentAnswer;
    private Handler mHandler;
    private Test[] examples;
    private TextReader textReader;
    private OnErrorListener onErrorListener;

    AbstractChatBot(Context context, AssetManager am, String fileName, int examplesNum) {
        super(TAG);
        textReader = new TextReader(am, fileName, examplesNum);
        this.examplesNum = examplesNum;
        greetingMsg = context.getResources().getString(R.string.greeting);
        rightAnswerMsg = context.getResources().getString(R.string.rightAnswer);
        wrongAnswerMsg = context.getResources().getString(R.string.wrongAnswer);
        nextAnswerMsg = context.getResources().getString(R.string.nextAnswer);
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

    private boolean checkAllExamples() {
        for (Test example : examples) {
            if (!example.checkAllAnswers()) {
                return false;
            }
        }
        return true;
    }

    private void process(String input) {
        try {
            if (input.isEmpty()) {
                send(greetingMsg);
                loadExamples();
                currentExample = 0;
                String msg = examples[currentExample].getQuestion();
                send(msg);
            } else {
                examples[currentExample].setUserAnswer(currentAnswer++, input);
                if (currentAnswer < examples[currentExample].getNumberOfAnswers()) {
                    send(nextAnswerMsg);
                } else {
                    currentAnswer = 0;
                    currentExample = (currentExample + 1) % examplesNum;
                    if (currentExample == 0) {
                        boolean resultOk = checkAllExamples();
                         if (resultOk) {
                             send(rightAnswerMsg);
                             loadExamples();
                         } else {
                             send(wrongAnswerMsg);
                         }
                    }
                    String msg = examples[currentExample].getQuestion();
                    send(msg);
                }
            }
        } catch (ChatBotException e) {
            Log.e(TAG, ERROR, e);
            if (onErrorListener != null) {
                onErrorListener.onError(e.getMessage());
            }
            quit();
        }
    }


    private void loadExamples() throws ChatBotException {
        String[] lines;
        try {
            lines = textReader.readRandomLines();
        } catch (IOException e) {
            throw new ChatBotException(e.getMessage(), e);
        }

        examples = new Test[lines.length];
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String[] parts = line.split(Constants.STRING_DELIMITER);
            if (parts.length < 2) {
                throw new ChatBotException(CONTENT_ERROR);
            }
            examples[i] = new Test(parts[0], Arrays.copyOfRange(parts, 1, parts.length));
        }
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
