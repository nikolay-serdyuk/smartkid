package com.nserdyuk.smartkid.tasks;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.gson.Gson;
import com.nserdyuk.smartkid.R;
import com.nserdyuk.smartkid.io.TextReader;
import com.nserdyuk.smartkid.models.Example;
import com.nserdyuk.smartkid.tasks.base.Bot;
import com.nserdyuk.smartkid.tasks.base.BotException;

import java.io.IOException;

abstract class ChatBot extends Bot {
    private static final String TAG = ChatBot.class.getName();
    private static final String ERROR = "An error occurred in bot";
    private static final String START_MESSAGE = "";
    private static final Gson GSON = new Gson();

    private final String greetingMsg;
    private final String rightAnswerMsg;
    private final String wrongAnswerMsg;
    private final int examplesNum;
    private final TextReader textReader;

    private int currentExample;
    private int currentAnswer;
    private Test[] tests;

    ChatBot(Context context, AssetManager am, String fileName, int examplesNum) {
        super(TAG);

        textReader = new TextReader(am, fileName, examplesNum);
        this.examplesNum = examplesNum;
        greetingMsg = context.getResources().getString(R.string.greeting);
        rightAnswerMsg = context.getResources().getString(R.string.right_answer);
        wrongAnswerMsg = context.getResources().getString(R.string.wrong_answer);
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

        try {
            String inputStr = (String) obj;
            String msg;
            if (START_MESSAGE.equals(inputStr)) {
                send(greetingMsg);
                loadExamples();
                currentExample = 0;
            } else {
                tests[currentExample].setUserAnswer(currentAnswer++, inputStr);
                if (currentAnswer < tests[currentExample].getNumberOfAnswers()) {
                    // There could be more answers for this question.
                    // E.g. divisor and remainder. Let's ask to input them.
                    msg = tests[currentExample].getExample().getAnswers()[currentAnswer].getHint();
                    send(msg);
                    return;
                } else {
                    currentAnswer = 0;
                    currentExample = (currentExample + 1) % examplesNum;
                    if (currentExample == 0) {
                        // We have got answers for all the shown examples. Let's check them.
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

            msg = tests[currentExample].getExample().getQuestion();
            send(msg);
            msg = tests[currentExample].getExample().getAnswers()[currentAnswer].getHint();
            send(msg);
        } catch (BotException e) {
            Log.e(TAG, ERROR, e);
            onError(e);
        }
    }

    private void loadExamples() throws BotException {
        String[] lines;
        try {
            lines = textReader.readRandomLines();
        } catch (IOException e) {
            throw new BotException(e.getMessage(), e);
        }

        tests = new Test[lines.length];
        for (int i = 0; i < lines.length; i++) {
            tests[i] = new Test(GSON.fromJson(lines[i], Example.class));
        }
    }

    private boolean checkAllExamples() {
        for (Test test : tests) {
            if (!test.checkAllAnswers()) {
                return false;
            }
        }
        return true;
    }

    private class Test {
        private final Example example;
        private final String[] userAnswers;

        Test(Example example) {
            this.example = example;
            this.userAnswers = new String[example.getAnswers().length];
        }

        void setUserAnswer(int n, String answer) {
            // A special case, when user inputs many zeroes,
            // in the beginning, e.g. 0043, or at the end, e.g. 43.0.
            // They should be cleared.
            // Problem values: 0.28
            String newAnswer = answer.replaceFirst("^0*([1-9])", "$1").replaceAll("\\.(0*)?$", "");
            userAnswers[n] = newAnswer;
        }

        Example getExample() {
            return example;
        }

        int getNumberOfAnswers() {
            return userAnswers.length;
        }

        boolean checkAllAnswers() {
            for (int i = 0; i < userAnswers.length; i++) {
                if (!userAnswers[i].equals(example.getAnswers()[i].getValue())) {
                    return false;
                }
            }
            return true;
        }
    }
}