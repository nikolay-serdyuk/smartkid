package com.nserdyuk.smartkid.tasks;

import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import android.widget.Toast;

import com.nserdyuk.smartkid.R;
import com.nserdyuk.smartkid.common.Constants;
import com.nserdyuk.smartkid.common.Dialogs;
import com.nserdyuk.smartkid.io.ImageReader;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/*
    Common guide lines
    http://blog.danlew.net/2014/11/19/styles-on-android/
    https://jeroenmols.com/blog/2016/03/07/resourcenaming/

    0. Проверить что в xml файлах нет предупреждений
    1. Проверить что задания расположены в правильном порядке
    2. вставить @NonNull
    3. unit tests
    5. протестировать с входом и выходом из спящего режима
    6. Проверить как выглядет на смартфоне
    7. Запретить поворот приложения
    8. PDB check
    9. check adb logcat Runtime Exceptions
*/

public class ChatActivity extends AbstractCommunicationActivity {
    private static final String TAG = "ChatActivity";
    private static final String ERROR_LOAD_IMAGES = "An error occurred while loading images";
    private static final String ERROR_INVALID_PARAMETER = "An error occurred while reading extended data from intent";
    private static final int DEFAULT_MARGIN = 15;
    private static final int ANIMATION_DURATION = 1000;

    private int colorLeftBubble;
    private int colorRightBubble;
    private String rightAnswerMsg;

    private AbstractChatBot chatBot;
    private EditText editText;
    private LayoutInflater layoutInflater;
    private ScrollView scrollView;
    private LinearLayout svLinearLayout;
    private TextView title;

    private String titleMessage;
    private int correctAnswers;

    @Override
    public void send(Object object) {
        chatBot.receive(object);
    }

    @Override
    public void onBackPressed() {
        Dialogs.showExitDialog(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatBot.quit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rightAnswerMsg = getResources().getString(R.string.rightAnswer);
        colorLeftBubble = ContextCompat.getColor(this, R.color.leftBubble);
        colorRightBubble = ContextCompat.getColor(this, R.color.rightBubble);

        layoutInflater = getLayoutInflater();
        scrollView = (ScrollView) findViewById(R.id.sv_activity_chat);
        svLinearLayout = (LinearLayout) findViewById(R.id.ll_sv_activity_chat);

        editText = (EditText) findViewById(R.id.et_activity_chat);
        editText.setOnKeyListener(new OnKeyListener());

        try {
            setBackgroundImage();
        } catch (IOException e) {
            Log.e(TAG, ERROR_LOAD_IMAGES, e);
            showError(ERROR_LOAD_IMAGES);
            return;
        }

        titleMessage = getIntent().getStringExtra(Constants.ATTRIBUTE_TITLE);
        title = (TextView) findViewById(R.id.tv_activity_chat_title);
        updateTitle();

        String fileName = getIntent().getStringExtra(Constants.ATTRIBUTE_FILE);
        if (StringUtils.isBlank(fileName)) {
            showError(ERROR_INVALID_PARAMETER);
        }
        int examplesNum = getIntent().getIntExtra(Constants.ATTRIBUTE_EXAMPLES, 0);
        chatBot = new AbstractChatBot(this, getAssets(), fileName, examplesNum) {
            @Override
            public void send(Object object) {
                ChatActivity.this.receive(object);
            }
        };
        chatBot.setOnErrorListener(new AbstractChatBot.OnErrorListener() {

            @Override
            public void onError(String message) {
                ChatActivity.this.runOnUiThread(new ErrorReporter(message));
            }

        });
        chatBot.start();
    }

    @Override
    protected void process(Object o) {
        if (o instanceof String) {
            String str = (String) o;
            drawBubble(new RightBubble(str));
            if (rightAnswerMsg.equals(str)) {
                correctAnswers++;
                updateTitle();
            }
        }
    }

    private void updateTitle() {
        String t;
        if (correctAnswers > 0) {
            t = String.format(Locale.US, Constants.RIGHT_ANSWER_FORMAT, titleMessage, correctAnswers);
        } else {
            t = titleMessage;
        }
        title.setText(t);
    }

    private void setBackgroundImage() throws IOException {
        String picMask = getIntent().getStringExtra(Constants.ATTRIBUTE_PICS_MASK);
        if (picMask == null) {
            picMask = Constants.DEFAULT_PICS_MASK;
        }
        InputStream is = new ImageReader().readRandomImage(getAssets(), picMask);
        ImageView myImage = (ImageView) findViewById(R.id.iv_activity_chat);
        myImage.setImageDrawable(Drawable.createFromStream(is, null));
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
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
        textView.setText(bubble.message);

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator.ofInt(scrollView, "scrollY", svLinearLayout.getBottom())
                        .setDuration(ANIMATION_DURATION).start();
            }
        });
    }

    private static class Bubble {
        private final int image;
        private final int textColor;
        private final String message;
        private final int gravity;
        private final int leftMargin;
        private final int rightMargin;

        Bubble(int image, int textColor, int gravity, int leftMargin, int rightMargin, String message) {
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
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    String msg = editText.getText().toString();
                    if (!msg.isEmpty()) {
                        drawBubble(new LeftBubble(msg));
                        editText.setText("");
                        send(msg);
                    }
                    return true;
                }
            }
            return false;
        }

    }

    private class ErrorReporter implements Runnable {
        private final String m;

        public ErrorReporter(String m) {
            this.m = m;
        }

        @Override
        public void run() {
            showError(m);
        }
    }
}
