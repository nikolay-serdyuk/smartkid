package com.nserdyuk.smartkid.tasks;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.nserdyuk.smartkid.R;
import com.nserdyuk.smartkid.common.Complexity;
import com.nserdyuk.smartkid.common.Constants;
import com.nserdyuk.smartkid.common.Point;
import com.nserdyuk.smartkid.views.Grid2dView;

public class Grid2dActivity extends AbstractCommunicationActivity implements ICommunication {

    private final static int COLOR_BACKGROUND = Color.WHITE;
    private final static int COLOR_TEXT_TITLE = Color.BLACK;

    private Grid2dView grid2dView;
    private TextView textView;
    private AbstractGrid2dBot bot;
    private boolean greetingMsgPassed;

    @Override
    public void send(Object object) {
        bot.receive(object);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bot.quit();
    }

    @Override
    protected void process(Object o) {
        if (o instanceof String) {
            if (greetingMsgPassed) {
                grid2dView.setClickable(true);
            }
            greetingMsgPassed = true;
            String str = (String) o;
            textView.setText(str);
        }
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
                grid2dView.setClickable(false);
                send(point);
            }
        });

        int examplesNum = getIntent().getIntExtra(Constants.ATTRIBUTE_EXAMPLES, 0);
        Complexity complexity = Complexity.valueOf(getIntent().getStringExtra(Constants.ATTRIBUTE_COMPLEXITY));
        bot = new AbstractGrid2dBot(this, examplesNum, complexity, grid2dView.getRows(), grid2dView.getColumns()) {
            @Override
            public void send(Object object) {
                Grid2dActivity.this.receive(object);
            }
        };
        bot.start();
        grid2dView.setClickable(false);
    }

}