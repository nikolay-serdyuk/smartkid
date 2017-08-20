package com.nserdyuk.smartkid.tasks;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.nserdyuk.smartkid.R;
import com.nserdyuk.smartkid.common.Constants;
import com.nserdyuk.smartkid.io.TextReader;
import com.nserdyuk.smartkid.tasks.base.BotException;
import com.nserdyuk.smartkid.tasks.base.Bot;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Arrays;

public abstract class ChatBot extends Bot {
    private static final String TAG = ChatBot.class.getName();
    private static final String ERROR = "An error occurred in bot";
    private static final String CONTENT_ERROR = "Invalid content";
    private static final String START_MESSAGE = "";

    private final String greetingMsg;
    private final String rightAnswerMsg;
    private final String wrongAnswerMsg;
    private final String nextAnswerMsg;
    private final int examplesNum;
    private final TextReader textReader;

    private int currentExample;
    private int currentAnswer;
    private Test[] examples;

    public ChatBot(Context context, AssetManager am, String fileName, int examplesNum) {
        super(TAG);

        textReader = new TextReader(am, fileName, examplesNum);
        this.examplesNum = examplesNum;
        greetingMsg = context.getResources().getString(R.string.greeting);
        rightAnswerMsg = context.getResources().getString(R.string.rightAnswer);
        wrongAnswerMsg = context.getResources().getString(R.string.wrongAnswer);
        nextAnswerMsg = context.getResources().getString(R.string.nextAnswer);
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

        String input = (String)obj;
        try {
            if (START_MESSAGE.equals(input)) {
                send(greetingMsg);
                loadExamples();
                currentExample = 0;
            } else {
                examples[currentExample].setUserAnswer(currentAnswer++, input);
                if (currentAnswer < examples[currentExample].getNumberOfAnswers()) {
                    send(nextAnswerMsg);
                    return;
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
                }
            }

            String msg = examples[currentExample].getQuestion();
            send(msg);
        } catch (BotException e) {
            Log.e(TAG, ERROR, e);
            onError(e);
        }
    }

    private boolean checkAllExamples() {
        for (Test example : examples) {
            if (!example.checkAllAnswers()) {
                return false;
            }
        }
        return true;
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
            examples[i] = new Test(parts[0], Arrays.copyOfRange(parts, 1, parts.length));
        }
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

}