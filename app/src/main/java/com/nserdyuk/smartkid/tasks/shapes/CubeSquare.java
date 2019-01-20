package com.nserdyuk.smartkid.tasks.shapes;

import javax.microedition.khronos.opengles.GL10;

public class CubeSquare implements Shape {
    private final Shape[] cubeLines;

    public CubeSquare(float x, float y, float z, int size) {
        cubeLines = new CubeLine[size];
        for (int i = 0; i < size; i++) {
            cubeLines[i] = new CubeLine(x, y + i * 2, z, size);
        }
    }

    @Override
    public void draw(GL10 gl) {
        for (Shape cubeLine : cubeLines) {
            cubeLine.draw(gl);
        }
    }

    @Override
    public boolean getVisible() {
        boolean visible = false;
        for (Shape cubeLine : cubeLines) {
            visible |= cubeLine.getVisible();
        }
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        for (Shape cubeLine : cubeLines) {
            cubeLine.setVisible(visible);
        }
    }

    @Override
    public void setRandomVisibility() {
        for (Shape cubeLine : cubeLines) {
            cubeLine.setRandomVisibility();
        }
    }
}
