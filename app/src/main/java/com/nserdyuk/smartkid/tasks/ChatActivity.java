package com.nserdyuk.smartkid.tasks;

import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nserdyuk.smartkid.R;
import com.nserdyuk.smartkid.common.Constants;
import com.nserdyuk.smartkid.common.Dialogs;
import com.nserdyuk.smartkid.common.Utils;
import com.nserdyuk.smartkid.io.ImageReader;
import com.nserdyuk.smartkid.tasks.base.Bot;
import com.nserdyuk.smartkid.tasks.base.CommunicationActivity;

import java.util.Locale;

public class ChatActivity extends CommunicationActivity {
    private static final String ERROR_LOAD_IMAGES = "An error occurred while loading images";
    private static final int DEFAULT_MARGIN = 15;
    private static final int ANIMATION_DURATION = 1000;

    private int colorLeftBubble;
    private int colorRightBubble;
    private String rightAnswerMsg;

    private Bot bot;
    private EditText editText;
    private LayoutInflater layoutInflater;
    private ScrollView scrollView;
    private LinearLayout svLinearLayout;
    private TextView title;
    private ImageView imageView;

    private String titleMessage;
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
    protected void send(Object object) {
        bot.receive(object);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rightAnswerMsg = getResources().getString(R.string.right_answer);
        colorLeftBubble = ContextCompat.getColor(this, R.color.activity_chat_left_bubble);
        colorRightBubble = ContextCompat.getColor(this, R.color.activity_chat_right_bubble);

        layoutInflater = getLayoutInflater();
        scrollView = (ScrollView) findViewById(R.id.sv_activity_chat);
        svLinearLayout = (LinearLayout) findViewById(R.id.ll_sv_activity_chat);

        editText = (EditText) findViewById(R.id.et_activity_chat);
        editText.setOnKeyListener(new OnKeyListener());

        titleMessage = getIntent().getStringExtra(Constants.ATTRIBUTE_TITLE);
        title = (TextView) findViewById(R.id.tv_activity_chat_title);
        updateTitle();

        String fileName = getIntent().getStringExtra(Constants.ATTRIBUTE_FILE);
        examplesNum = getIntent().getIntExtra(Constants.ATTRIBUTE_EXAMPLES, 0);
        bot = new ChatBot(this, getAssets(), fileName, examplesNum) {

            @Override
            public void send(Object object) {
                ChatActivity.this.receive(object);
            }
        };
        bot.setOnErrorListener(e -> Utils.showErrorInUiThread(this, e.getMessage()));
        bot.start();
        setBackgroundImage();
    }

    @Override
    protected void onMessage(Object o) {
        if (o instanceof String) {
            String str = (String) o;
            drawBubble(new RightBubble(str));
            if (rightAnswerMsg.equals(str)) {
                correctAnswers += examplesNum;
                updateTitle();
            }
        }
    }

    private void setBackgroundImage() {
        imageView = (ImageView) findViewById(R.id.iv_activity_chat);
        String extra = getIntent().getStringExtra(Constants.ATTRIBUTE_FILE_MASK);
        String picMask = extra != null ? extra : Constants.DEFAULT_PICS_MASK;

        ImageReader ir = new ImageReader(getAssets()) {

            @Override
            protected void onPostExecute(Drawable drawable) {
                if (drawable != null) {
                    imageView.setImageDrawable(drawable);
                }
            }
        };
        ir.setOnErrorListener(e -> Utils.showErrorInUiThread(this, ERROR_LOAD_IMAGES));
        ir.execute(picMask);
    }

    private void updateTitle() {
        String t;
        if (correctAnswers > 0) {
            t = String.format(Locale.US, Constants.RIGHT_ANSWER_FORMAT, titleMessage,
                    correctAnswers);
        } else {
            t = titleMessage;
        }
        title.setText(t);
    }

    private void drawBubble(Bubble bubble) {
        View item = layoutInflater.inflate(R.layout.activity_chat_list_item, svLinearLayout, false);
        item.setBackgroundResource(bubble.image);
        LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) item.getLayoutParams();
        llp.gravity = bubble.gravity;
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) item.getLayoutParams();
        mlp.setMargins(bubble.leftMargin, mlp.topMargin, bubble.rightMargin, mlp.bottomMargin);
        item.setLayoutParams(llp);
        item.requestLayout();
        svLinearLayout.addView(item);

        TextView textView = (TextView) item.findViewById(R.id.tv_activity_chat_list_item);
        textView.setTextColor(bubble.textColor);
        textView.setText(Html.fromHtml(bubble.message, Html.FROM_HTML_MODE_LEGACY),
                TextView.BufferType.SPANNABLE);

        scrollView.post(() ->
                ObjectAnimator.ofInt(scrollView, "scrollY", svLinearLayout.getBottom())
                        .setDuration(ANIMATION_DURATION)
                        .start());
    }

    private static class Bubble {
        private final int image;
        private final int textColor;
        private final String message;
        private final int gravity;
        private final int leftMargin;
        private final int rightMargin;

        Bubble(int image, int textColor, int gravity, int leftMargin, int rightMargin,
                String message) {
            this.image = image;
            this.textColor = textColor;
            this.gravity = gravity;
            this.leftMargin = leftMargin;
            this.rightMargin = rightMargin;
            this.message = message;
        }
    }

    private class LeftBubble extends Bubble {
        LeftBubble(String message) {
            super(R.drawable.b1_left, colorLeftBubble, Gravity.START, DEFAULT_MARGIN, 0, message);
        }
    }

    private class RightBubble extends Bubble {
        RightBubble(String message) {
            super(R.drawable.b1_right, colorRightBubble, Gravity.END, 0, DEFAULT_MARGIN, message);
        }
    }

    private class OnKeyListener implements View.OnKeyListener {

        @Override
        public boolean onKey(View view, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                String msg = editText.getText().toString();
                if (!msg.isEmpty()) {
                    drawBubble(new LeftBubble(msg));
                    editText.setText("");
                    send(msg);
                }
                return true;
            }
            return false;
        }

    }

}
