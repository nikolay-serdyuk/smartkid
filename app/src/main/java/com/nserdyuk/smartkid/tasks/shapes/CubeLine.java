package com.nserdyuk.smartkid.tasks.shapes;

import javax.microedition.khronos.opengles.GL10;

public class CubeLine implements Shape {
    private final Shape[] cubes;

    public CubeLine(float x, float y, float z, int cubesNum) {
        cubes = new Shape[cubesNum];
        for (int i = 0; i < cubesNum; i++) {
            cubes[i] = new Cube(x + i * 2, y, z);
        }
    }

    @Override
    public void draw(GL10 gl) {
        for (Shape cube : cubes) {
            cube.draw(gl);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        for (Shape cube : cubes) {
            cube.setVisible(visible);
        }
    }

    @Override
    public boolean getVisible() {
        boolean visible = false;
        for (Shape cube : cubes) {
            visible |= cube.getVisible();
        }
        return visible;
    }

    @Override
    public void setRandomVisibility() {
        for (Shape cube : cubes) {
            cube.setRandomVisibility();
        }
    }
}
