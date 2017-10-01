package com.nserdyuk.smartkid.tasks.shapes;

import javax.microedition.khronos.opengles.GL10;

public class CompositeCube implements Shape {
    private final Shape[] cubeSquares;

    public CompositeCube(float x, float y, float z, int size) {
        cubeSquares = new CubeSquare[size];
        for (int i = 0; i < size; i++) {
            cubeSquares[i] = new CubeSquare(x, y, z + i * 2, size);
        }
    }

    @Override
    public void draw(GL10 gl) {
        for (Shape cubeSquare : cubeSquares) {
            cubeSquare.draw(gl);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        for (Shape cubeSquare : cubeSquares) {
            cubeSquare.setVisible(visible);
        }
    }

    @Override
    public boolean getVisible() {
        boolean visible = false;
        for (Shape cubeSquare : cubeSquares) {
            visible |= cubeSquare.getVisible();
        }
        return visible;
    }

    @Override
    public void setRandomVisibility() {
        for (Shape cubeSquare : cubeSquares) {
            cubeSquare.setRandomVisibility();
        }
    }
}
