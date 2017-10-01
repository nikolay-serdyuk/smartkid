package com.nserdyuk.smartkid.tasks.shapes;

import javax.microedition.khronos.opengles.GL10;

public interface Shape {
    void draw(GL10 gl);
    void setVisible(boolean visible);
    boolean getVisible();
    void setRandomVisibility();
}
