package com.nserdyuk.smartkid.tasks;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.nserdyuk.smartkid.R;
import com.nserdyuk.smartkid.common.Constants;
import com.nserdyuk.smartkid.common.Dialogs;
import com.nserdyuk.smartkid.common.Utils;
import com.nserdyuk.smartkid.io.ImageReader;
import com.nserdyuk.smartkid.tasks.base.CommunicationActivity;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ExaminationActivity extends CommunicationActivity {
    private static final String PIC_MASK = "sheet";
    private static final String FONT_NAME = "fonts/SueEllenFrancisco.ttf";
    private static final String ERROR_LOAD_IMAGES = "An error occurred while loading images";
    private static final int ANSWER_FONT_SIZE = 35;
    private static final int BUTTON_MARGIN = 20;

    private Typeface typeface;
    private TextView title;
    private TextView mainText;
    private LinearLayout answerBlockLayout;

    private ExaminationBot bot;
    private String titleMessage;
    private String rightAnswerMsg;
    private String wrongAnswerMsg;
    private String greetingMsg;
    private int correctAnswers;
    private int examplesNum;


    @Override
    public void onBackPressed() {
        Dialogs.showExitDialog(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bot.quit();
    }

    @Override
    protected void send(Object msg) {
        bot.receive(msg);
    }

    @Override
    protected void onMessage(Object o) {
        if (o instanceof String) {
            String str = (String) o;
            mainText.setEnabled(true);
            answerBlockLayout.removeAllViews();
            if (rightAnswerMsg.equals(str) || wrongAnswerMsg.equals(str) || greetingMsg.equals(str)) {
                mainText.setText(str);
                if (rightAnswerMsg.equals(str)) {
                    correctAnswers += examplesNum;
                }
                updateTitle();
                return;
            }

            String parts[] = StringUtils.split(str, Constants.STRING_DELIMITER);
            mainText.setText(parts[0]);
            List<String> answersList = Arrays.asList(parts).subList(1, parts.length);
            Collections.shuffle(answersList);
            for (int i = 0; i < answersList.size(); i++) {
                addAnswerButton(answersList.get(i));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examination);

        answerBlockLayout = (LinearLayout)findViewById(R.id.ll_activity_examination_question_block);
        titleMessage = getIntent().getStringExtra(Constants.ATTRIBUTE_TITLE);
        title = (TextView) findViewById(R.id.tv_activity_examination_title);
        updateTitle();

        rightAnswerMsg = getResources().getString(R.string.right_answer_english);
        wrongAnswerMsg = getResources().getString(R.string.wrong_answer_english);
        greetingMsg = getResources().getString(R.string.greeting_english);

        mainText = (TextView) findViewById(R.id.tv_activity_examination_main_text);

        String fileName = getIntent().getStringExtra(Constants.ATTRIBUTE_FILE);
        examplesNum = getIntent().getIntExtra(Constants.ATTRIBUTE_EXAMPLES, 0);

        bot = new ExaminationBot(this, getAssets(), fileName, examplesNum) {
            @Override
            public void send(Object object) {
                ExaminationActivity.this.receive(object);
            }
        };
        bot.setOnErrorListener(e -> Utils.showErrorInUiThread(this, e.getMessage()));
        bot.start();

        setBackgroundImageAndFont();
    }

    private void updateTitle() {
        title.setText(correctAnswers == 0 ?
                titleMessage : String.format(Locale.US, Constants.RIGHT_ANSWER_FORMAT, titleMessage, correctAnswers));
    }

    private void addAnswerButton(String answer) {
        RadioButton button = new RadioButton(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, BUTTON_MARGIN, 0, BUTTON_MARGIN);
        button.setLayoutParams(params);
        button.setText(answer);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, ANSWER_FONT_SIZE);
        setStyledFont(button);
        button.setOnClickListener(v -> {
            mainText.setEnabled(false);
            disableAllButtons();
            send(((RadioButton) v).getText());
        });
        answerBlockLayout.addView(button);
    }

    private void disableAllButtons() {
        for (int i = 0; i < answerBlockLayout.getChildCount(); i++) {
            View child = answerBlockLayout.getChildAt(i);
            child.setEnabled(false);
        }
    }

    private void setBackgroundImageAndFont() {
        typeface = Typeface.createFromAsset(getAssets(), FONT_NAME);
        setStyledFont(title);
        setStyledFont(mainText);

        ImageReader ir = new ImageReader(getAssets()) {

            @Override
            protected void onPostExecute(Drawable drawable) {
                if (drawable != null) {
                    LinearLayout ll = (LinearLayout) findViewById(R.id.ll_activity_examination);
                    ll.setBackground(drawable);
                }
            }
        };
        ir.setOnErrorListener(e -> Utils.showErrorInUiThread(this, ERROR_LOAD_IMAGES));
        ir.execute(PIC_MASK);
    }

    private void setStyledFont(TextView w) {
        if (typeface != null) {
            w.setTypeface(typeface);
        }
    }
}