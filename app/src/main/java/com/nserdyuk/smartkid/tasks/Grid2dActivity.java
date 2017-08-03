package com.nserdyuk.smartkid.tasks;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.nserdyuk.smartkid.R;
import com.nserdyuk.smartkid.common.Constants;
import com.nserdyuk.smartkid.common.Point;
import com.nserdyuk.smartkid.views.Grid2dView;

public class Grid2dActivity extends Activity implements IChat {

    private final static int COLOR_BACKGROUND = Color.WHITE;
    private final static int COLOR_TEXT_TITLE = Color.BLACK;

    private volatile Handler handler;
    private Grid2dView grid2dView;
    private TextView textView;
    private AbstractGrid2dBot bot;

    @Override
    public void send(Object object) {
        bot.receive(object);
    }

    @Override
    public void receive(Object object) {
        Message m = handler.obtainMessage();
        m.obj = object;
        m.sendToTarget();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bot.quit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid2d);
        textView = (TextView) findViewById(R.id.tv_activity_grid2d);
        textView.setBackgroundColor(COLOR_BACKGROUND);
        textView.setTextColor(COLOR_TEXT_TITLE);

        grid2dView = (Grid2dView)findViewById(R.id.grid_activity_grid2d);
        grid2dView.setBackgroundColor(COLOR_BACKGROUND);
        grid2dView.setOnTouchListener(new Grid2dView.OnTouchListener() {
            @Override
            public void onTouch(Point point) {
                Log.d("DBG", "DBG: " + point.getX() + " " + point.getY());
            }
        });

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj instanceof Point) {
                    Point point = (Point) msg.obj;
                }
            }
        };

        int examplesNum = getIntent().getIntExtra(Constants.ATTRIBUTE_EXAMPLES, 0);
        bot = new AbstractGrid2dBot(examplesNum) {
            @Override
            public void send(Object object) {
                Grid2dActivity.this.receive(object);
            }
        };
        bot.start();
    }

}