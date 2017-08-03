package com.nserdyuk.smartkid.tasks;

import com.nserdyuk.smartkid.common.Point;

public abstract class AbstractGrid2dBot extends AbstractBot {
    private static final String TAG = "AbstractGrid2dBot";
    private static final Point INVALID_POINT = new Point(-1, -1);

    public AbstractGrid2dBot(int examplesNum) {
        super(TAG);
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
    }
}
