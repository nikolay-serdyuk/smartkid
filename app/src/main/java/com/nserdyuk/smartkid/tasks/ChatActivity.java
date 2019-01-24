package com.nserdyuk.smartkid.tasks;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import java.util.Random;

public class ChatActivity extends CommunicationActivity {
    private static final String ERROR_LOAD_IMAGES = "An error occurred while loading images";
    private static final int DEFAULT_MARGIN = 35;
    private static final int ANIMATION_DURATION = 1000;
    private static final Random RANDOM = new Random();

    private final ColorScheme[] colorSchemes = {
            new ColorScheme(
                    "#124559",
                    "#ffffff",
                    "#ffffff",
                    "#01161e",
                    "#ffffff",
                    "wallpaper_branches.png",
                    getRandomElement(new int[]{R.drawable.animal2, R.drawable.animal3}))
    };
    private final ColorScheme colorScheme = colorSchemes[RANDOM.nextInt(colorSchemes.length)];

    private String rightAnswerMsg;

    private Bot bot;
    private EditText editText;
    private LayoutInflater layoutInflater;
    private ScrollView scrollView;
    private LinearLayout svLinearLayout;
    private TextView title;

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

        layoutInflater = getLayoutInflater();
        scrollView = (ScrollView) findViewById(R.id.sv_activity_chat);
        svLinearLayout = (LinearLayout) findViewById(R.id.ll_sv_activity_chat);

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl_activity_chat_input_window);
        rl.setBackgroundColor(colorScheme.backgroundColor);

        editText = (EditText) findViewById(R.id.et_activity_chat);
        editText.setOnKeyListener(new OnKeyListener());

        titleMessage = getIntent().getStringExtra(Constants.ATTRIBUTE_TITLE);
        title = (TextView) findViewById(R.id.tv_activity_chat_title);
        title.setTextColor(colorScheme.titleColor);
        title.setBackgroundColor(colorScheme.titleBackgroundColor);
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
            drawBubble(new LeftBubble(str));
            if (rightAnswerMsg.equals(str)) {
                correctAnswers += examplesNum;
                updateTitle();
            }
        }
    }

    private void setBackgroundImage() {
        ImageReader ir = new ImageReader(getAssets()) {

            @Override
            protected void onPostExecute(BitmapDrawable drawable) {
                if (drawable != null) {
                    drawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                    getWindow().setBackgroundDrawable(drawable);
                }
            }
        };
        ir.setOnErrorListener(e -> Utils.showErrorInUiThread(this, ERROR_LOAD_IMAGES));
        ir.execute(colorScheme.backgroundImage);
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
        LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) item.getLayoutParams();
        llp.gravity = bubble.gravity;
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) item.getLayoutParams();
        mlp.setMargins(bubble.leftMargin, mlp.topMargin, bubble.rightMargin, mlp.bottomMargin);
        item.setLayoutParams(llp);
        item.requestLayout();
        svLinearLayout.addView(item);

        if (bubble.avatarImage != 0) {
            ImageView imageView = (ImageView) item.findViewById(R.id.iv_activity_chat_list_item);
            imageView.setImageResource(bubble.avatarImage);
        }

        LinearLayout ll = (LinearLayout) item.findViewById(R.id.rl_activity_chat_list_item_bubble);
        ll.setBackgroundResource(bubble.bubbleImage);

        TextView textView = (TextView) item.findViewById(R.id.tv_activity_chat_list_item_bubble);
        textView.setTextColor(bubble.textColor);
        textView.setText(Html.fromHtml(bubble.message, Html.FROM_HTML_MODE_LEGACY),
                TextView.BufferType.SPANNABLE);

        scrollView.post(() ->
                ObjectAnimator.ofInt(scrollView, "scrollY", svLinearLayout.getBottom())
                        .setDuration(ANIMATION_DURATION)
                        .start());
    }

    private static int getRandomElement(int[] array) {
        return array[RANDOM.nextInt(array.length)];
    }

    private static class Bubble {
        private final int avatarImage;
        private final int bubbleImage;
        private final int textColor;
        private final String message;
        private final int gravity;
        private final int leftMargin;
        private final int rightMargin;

        Bubble(int avatarImage, int bubbleImage, int textColor, int gravity, int leftMargin,
                int rightMargin,
                String message) {
            this.avatarImage = avatarImage;
            this.bubbleImage = bubbleImage;
            this.textColor = textColor;
            this.gravity = gravity;
            this.leftMargin = leftMargin;
            this.rightMargin = rightMargin;
            this.message = message;
        }
    }

    private class LeftBubble extends Bubble {
        LeftBubble(String message) {
            super(colorScheme.avatarImage, R.drawable.b3_left, colorScheme.leftBubbleColorText,
                    Gravity.START, DEFAULT_MARGIN, 0, message);
        }
    }

    private class RightBubble extends Bubble {
        RightBubble(String message) {
            super(0, R.drawable.b3_right, colorScheme.rightBubbleColorText, Gravity.END, 0,
                    DEFAULT_MARGIN, message);
        }
    }

    private class OnKeyListener implements View.OnKeyListener {

        @Override
        public boolean onKey(View view, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                String msg = editText.getText().toString();
                if (!msg.isEmpty()) {
                    drawBubble(new RightBubble(msg));
                    editText.setText("");
                    send(msg);
                }
                return true;
            }
            return false;
        }
    }

    private final static class ColorScheme {
        private final int leftBubbleColorText;
        private final int rightBubbleColorText;
        private final int titleColor;
        private final int titleBackgroundColor;
        private final int backgroundColor;
        private final String backgroundImage;
        private final int avatarImage;

        private ColorScheme(String leftBubbleColorText, String rightBubbleColorText,
                String titleColor, String titleBackgroundColorText, String backgroundColor,
                String backgroundImage, int avatarImage) {
            this.leftBubbleColorText = Color.parseColor(leftBubbleColorText);
            this.rightBubbleColorText = Color.parseColor(rightBubbleColorText);
            this.titleColor = Color.parseColor(titleColor);
            this.titleBackgroundColor = Color.parseColor(titleBackgroundColorText);
            this.backgroundColor = Color.parseColor(backgroundColor);
            this.backgroundImage = backgroundImage;
            this.avatarImage = avatarImage;
        }
    }

}
