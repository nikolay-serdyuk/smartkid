package com.nserdyuk.smartkid.tasks;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.nserdyuk.smartkid.R;
import com.nserdyuk.smartkid.common.Constants;
import com.nserdyuk.smartkid.common.Utils;
import com.nserdyuk.smartkid.io.TextReader;
import com.nserdyuk.smartkid.tasks.base.Bot;
import com.nserdyuk.smartkid.tasks.base.BotException;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public abstract class ExaminationBot extends Bot {
    private static final String TAG = ExaminationBot.class.getName();
    private static final String ERROR = "An error occurred in bot";
    private static final String CONTENT_ERROR = "Invalid content";
    private static final String START_MESSAGE = "";

    private static final int SHORT_DELAY = 1500;
    private static final int LONG_DELAY = 2000;

    private final String greetingMsg;
    private final String rightAnswerMsg;
    private final String wrongAnswerMsg;
    private final int examplesNum;
    private final TextReader textReader;

    private Test[] examples;
    private int currentExample;

    public ExaminationBot(Context context, AssetManager am, String fileName, int examplesNum) {
        super(TAG);

        textReader = new TextReader(am, fileName, examplesNum);
        this.examplesNum = examplesNum;
        greetingMsg = context.getResources().getString(R.string.greeting_english);
        rightAnswerMsg = context.getResources().getString(R.string.right_answer_english);
        wrongAnswerMsg = context.getResources().getString(R.string.wrong_answer_english);
    }

    @Override
    protected void onStart() {
        onMessage(START_MESSAGE);
    }

    @Override
    protected void onMessage(Object obj) {
        if (!(obj instanceof String)) {
            return;
        }

        Utils.delay(SHORT_DELAY);
        String input = (String)obj;
        try {
            if (START_MESSAGE.equals(input)) {
                sendWithDelay(greetingMsg, LONG_DELAY);
                loadExamples();
                currentExample = 0;
            } else {
                examples[currentExample++].setUserAnswer(input);
                if (currentExample == examplesNum) {
                    currentExample = 0;
                    boolean resultOk = checkAllExamples();
                    if (resultOk) {
                        sendWithDelay(rightAnswerMsg, LONG_DELAY);
                        loadExamples();
                    } else {
                        sendWithDelay(wrongAnswerMsg, LONG_DELAY);
                    }
                }
            }
            String msg = examples[currentExample].getQuestionAndAnswers();
            send(msg);
        } catch (BotException e) {
            Log.e(TAG, ERROR, e);
            onError(e);
        }
    }

    private boolean checkAllExamples() {
        for (Test example : examples) {
            if (!StringUtils.split(example.getQuestionAndAnswers(), Constants.STRING_DELIMITER)[1].equals(example.getUserAnswers())) {
                return false;
            }
        }
        return true;
    }

    private void sendWithDelay(String msg, long millis) {
        send(msg);
        Utils.delay(millis);
    }

    private void loadExamples() throws BotException {
        String[] lines;
        try {
            lines = textReader.readRandomLines();
        } catch (IOException e) {
            throw new BotException(e.getMessage(), e);
        }

        examples = new Test[lines.length];
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String[] parts = StringUtils.split(line, Constants.STRING_DELIMITER);
            if (parts.length < 2) {
                throw new BotException(CONTENT_ERROR);
            }
            examples[i] = new Test(line);
        }
    }

    private class Test {
        private final String questionAndAnswers;
        private String userAnswers;

        Test(String questionAndAnswers) {
            this.questionAndAnswers = questionAndAnswers;
        }

        String getQuestionAndAnswers() {
            return questionAndAnswers;
        }

        void setUserAnswer(String userAnswers) {
            this.userAnswers = userAnswers;
        }

        String getUserAnswers() {
            return userAnswers;
        }
    }

}