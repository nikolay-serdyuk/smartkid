package com.nserdyuk.smartkid.tasks.shapes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Cube implements Shape {
    private static final int LINE_WIDTH = 5;
    private static final int VISIBILITY_PROBABILITY_FACTOR = 4;

    private static final short TRIANGLE_BORDER_INDEX_LIST[] = {
            0, 1, 2, 3, 4, 5, 6, 7,
            0, 3, 1, 2, 4, 7, 5, 6,
            0, 4, 1, 5, 2, 6, 3, 7
    };

    private static final float EDGES[] = {
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
    };

    private static final float INITIAL_VERTICES[] = {
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
    };

    private static final float COLORS[] = {
            1.0f, 1.0f, 1.0f, 1.0f,
            0.7f, 0.7f, 0.7f, 1.0f,
            0.6f, 0.6f, 0.6f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            0.7f, 0.7f, 0.7f, 1.0f,
            0.6f, 0.6f, 0.6f, 1.0f,
    };

    private static final byte INDICES[] = {
            0, 4, 5, 0, 5, 1,
            1, 5, 6, 1, 6, 2,
            2, 6, 7, 2, 7, 3,
            3, 7, 4, 3, 4, 0,
            4, 7, 6, 4, 6, 5,
            3, 0, 1, 3, 1, 2
    };

    private static final Random RANDOM = new Random();

    private final FloatBuffer vertexBuffer;
    private final FloatBuffer colorBuffer;
    private final FloatBuffer colorEdgeBuffer;
    private final ByteBuffer indexBuffer;
    private final ShortBuffer triangleBorderIndicesBuffer;
    private final int triangleBorderIndices;

    private boolean visible;

    public Cube(float x, float y, float z) {
        visible = true;

        float[] vertices = new float[INITIAL_VERTICES.length];
        for (int i = 0; i < 8; i++) {
            vertices[i * 3] = INITIAL_VERTICES[i * 3] + x;
            vertices[i * 3 + 1] = INITIAL_VERTICES[i * 3 + 1] + y;
            vertices[i * 3 + 2] = INITIAL_VERTICES[i * 3 + 2] + z;
        }
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(INITIAL_VERTICES.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuf.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        byteBuf = ByteBuffer.allocateDirect(COLORS.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        colorBuffer = byteBuf.asFloatBuffer();
        colorBuffer.put(COLORS);
        colorBuffer.position(0);

        byteBuf = ByteBuffer.allocateDirect(EDGES.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        colorEdgeBuffer = byteBuf.asFloatBuffer();
        colorEdgeBuffer.put(EDGES);
        colorEdgeBuffer.position(0);

        indexBuffer = ByteBuffer.allocateDirect(INDICES.length);
        indexBuffer.put(INDICES);
        indexBuffer.position(0);

        triangleBorderIndices = TRIANGLE_BORDER_INDEX_LIST.length;
        ByteBuffer tbilByteBuffer = ByteBuffer.allocateDirect(
                TRIANGLE_BORDER_INDEX_LIST.length * 2);
        tbilByteBuffer.order(ByteOrder.nativeOrder());
        triangleBorderIndicesBuffer = tbilByteBuffer.asShortBuffer();
        triangleBorderIndicesBuffer.put(TRIANGLE_BORDER_INDEX_LIST);
        triangleBorderIndicesBuffer.position(0);
    }

    @Override
    public boolean getVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void setRandomVisibility() {
        if (RANDOM.nextInt(VISIBILITY_PROBABILITY_FACTOR) == 0) {
            setVisible(false);
        }
    }

    @Override
    public void draw(GL10 gl) {
        if (!visible) {
            return;
        }

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        gl.glDrawElements(GL10.GL_TRIANGLES, 36, GL10.GL_UNSIGNED_BYTE,
                indexBuffer);

        gl.glLineWidth(LINE_WIDTH);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorEdgeBuffer);
        gl.glDrawElements(GL10.GL_LINES, triangleBorderIndices,
                GL10.GL_UNSIGNED_SHORT, triangleBorderIndicesBuffer);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    }

}