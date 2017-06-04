package com.nserdyuk.smartkid.tasks.base;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.nserdyuk.smartkid.R;

import static java.lang.Thread.sleep;

/*
    Common guide lines
    http://blog.danlew.net/2014/11/19/styles-on-android/
    https://jeroenmols.com/blog/2016/03/07/resourcenaming/
 */
/*
public abstract class QAChatBot {
    public QAChatBot() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj instanceof String) {
                    String s = (String)msg.obj;
                    process(s);
                }
            }
        };
    }
    protected abstract void process(String msg);

    protected void recieve(String msg) {
        Message m = mHandler.obtainMessage();
        m.obj = msg;
        m.sendToTarget();
    };
}
*/

public abstract class AbstractChatActivity extends AppCompatActivity {
    private Handler mHandler;

    /*
    private static final ColorScheme[] Schemes = new ColorScheme[]{
            new ColorScheme(R.drawable.b1_11, R.drawable.b1_22, Color.parseColor("#29508E"),
                    Color.parseColor("#39555B")),
     };
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj instanceof String) {
                    String s = (String)msg.obj;
                    rightBubble(s);

                    //
                    try {
                        sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    s = "bye";
                    Log.d("BOT", s);
                    send(s);
                    //
                }
            }
        };
    }

    protected abstract void send(String msg);

    protected void recieve(String msg) {
        Message m = mHandler.obtainMessage();
        m.obj = msg;
        m.sendToTarget();
    };

    /*
    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                String msg = edit.getText().toString();
                if (!msg.isEmpty()) {
                    leftBubble(msg);
                    edit.setText("");
                    send(msg);
                }
                return true;
            }
        }
        return false;
    }
    */

    private void bubble(String msg, int msgColor, int img) {
    }

    private void leftBubble(String msg) {
    }

    private void rightBubble(String msg) {
    }

    private static class ColorScheme {
        private int leftBubble;
        private int rightBubble;
        private int leftBubbleText;
        private int rightBubbleText;

        ColorScheme(int l, int r, int lt, int rt) {
            leftBubble = l;
            rightBubble = r;
            leftBubbleText = lt;
            rightBubbleText = rt;
        }
    }
}
