package com.nserdyuk.smartkid.tasks;

import android.content.Context;

import com.nserdyuk.smartkid.R;
import com.nserdyuk.smartkid.common.Point;

public abstract class AbstractGrid2dBot extends AbstractBot {
    private static final String TAG = "AbstractGrid2dBot";
    private static final Point INVALID_POINT = new Point(-1, -1);

    private final String greetingMsg;
    private final String rightAnswerMsg;
    private final String wrongAnswerMsg;

    public AbstractGrid2dBot(Context context, int examplesNum) {
        super(TAG);
        greetingMsg = context.getResources().getString(R.string.greeting);
        rightAnswerMsg = context.getResources().getString(R.string.rightAnswer);
        wrongAnswerMsg = context.getResources().getString(R.string.wrongAnswer);
    }

    @Override
    protected void startBot() {
        process(INVALID_POINT);
    }

    @Override
    protected void process(Object o) {
        if (!(o instanceof Point)) {
            return;
        }
        if (INVALID_POINT.equals(o)) {
            send(greetingMsg);

        }
    }
}
