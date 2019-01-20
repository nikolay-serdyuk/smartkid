package com.nserdyuk.smartkid.tasks.shapes;

import javax.microedition.khronos.opengles.GL10;

public interface Shape {
    void draw(GL10 gl);

    boolean getVisible();

    void setVisible(boolean visible);

    void setRandomVisibility();
}
