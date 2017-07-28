package com.nserdyuk.smartkid.tasks;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
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
import com.nserdyuk.smartkid.io.ImageReader;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;

/*
    Common guide lines
    http://blog.danlew.net/2014/11/19/styles-on-android/
    https://jeroenmols.com/blog/2016/03/07/resourcenaming/

    0. вставить @NonNull
    1. unit tests
    2. check adb logcat Runtime Exceptions
    3. Проверить как выглядет на смартфоне
    4. Запретить поворот приложения
    5. PDB check
*/

public class ChatActivity extends AppCompatActivity implements IChat {
    private static final String TAG = "ChatActivity";
    private static final String ERROR_LOAD_IMAGES = "An error occurred while loading images";
    private static final String ERROR_INVALID_PARAMETER = "An error occurred while reading extended data from intent";
    private static final int LEFT_BUBBLE_TEXT_COLOR = Color.parseColor("#39555B");
    private static final int RIGHT_BUBBLE_TEXT_COLOR = Color.parseColor("#29508E");
    private static final int DEFAULT_MARGIN = 15;
    private static final int ANIMATION_DURATION = 1000;

    private Handler handler;
    private AbstractChatBot chatBot;
    private EditText editText;
    private LayoutInflater layoutInflater;
    private ScrollView scrollView;
    private LinearLayout svLinearLayout;

    @Override
    public void send(String msg) {
        chatBot.receive(msg);
    }

    @Override
    public void receive(String msg) {
        Message m = handler.obtainMessage();
        m.obj = msg;
        m.sendToTarget();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        layoutInflater = getLayoutInflater();
        scrollView = (ScrollView) findViewById(R.id.sv_activity_chat);
        svLinearLayout = (LinearLayout) findViewById(R.id.ll_sv_activity_chat);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj instanceof String) {
                    drawBubble(new RightBubble((String) msg.obj));
                }
            }
        };

        editText = (EditText) findViewById(R.id.et_activity_chat);
        editText.setOnKeyListener(new OnKeyListener());

        try {
            setBackgroundImage();
        } catch (IOException e) {
            Log.e(TAG, ERROR_LOAD_IMAGES, e);
            showError(ERROR_LOAD_IMAGES);
            return;
        }

        String fileName = getIntent().getStringExtra(Constants.ATTRIBUTE_FILE);
        if (StringUtils.isBlank(fileName)) {
            showError(ERROR_INVALID_PARAMETER);
        }
        int examplesNum = getIntent().getIntExtra(Constants.ATTRIBUTE_EXAMPLES, 0);
        chatBot = new AbstractChatBot(this, getAssets(), fileName, examplesNum) {
            @Override
            public void send(String msg) {
                ChatActivity.this.receive(msg);
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

    private static class LeftBubble extends Bubble {
        LeftBubble(String message) {
            super(R.drawable.b1_left, LEFT_BUBBLE_TEXT_COLOR, Gravity.START, DEFAULT_MARGIN, 0, message);
        }
    }

    private static class RightBubble extends Bubble {
        RightBubble(String message) {
            super(R.drawable.b1_right, RIGHT_BUBBLE_TEXT_COLOR, Gravity.END, 0, DEFAULT_MARGIN, message);
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
        private String m;

        public ErrorReporter(String m) {
            this.m = m;
        }

        @Override
        public void run() {
            showError(m);
        }
    }
}
