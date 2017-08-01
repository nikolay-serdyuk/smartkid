package com.nserdyuk.smartkid.tasks;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.nserdyuk.smartkid.R;
import com.nserdyuk.smartkid.common.Grid2dView;

public class Grid2D extends Activity {

    private final static int COLOR_BACKGROUND = Color.WHITE;
    private final static int COLOR_TEXT_TITLE = Color.BLACK;

    private Grid2dView grid2dView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid2d);
        textView = (TextView) findViewById(R.id.tv_activity_grid2d);
        textView.setBackgroundColor(COLOR_BACKGROUND);
        textView.setTextColor(COLOR_TEXT_TITLE);

        grid2dView = (Grid2dView)findViewById(R.id.grid_activity_grid2d);
        grid2dView.setBackgroundColor(COLOR_BACKGROUND);
    }

}