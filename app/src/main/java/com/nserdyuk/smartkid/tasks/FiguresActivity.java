package com.nserdyuk.smartkid.tasks;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.nserdyuk.smartkid.common.Dialogs;
import com.nserdyuk.smartkid.common.OnSwipeTouchListener;
import com.nserdyuk.smartkid.tasks.shapes.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;

public class FiguresActivity extends AppCompatActivity {
    private List<GLSurfaceView> viewList;
    private int viewListIndex;

    @Override
    public void onBackPressed() {
        Dialogs.showExitDialog(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        viewList = new ArrayList<>();
        GLSurfaceView view = createNewView();
        viewList.add(view);
        setContentView(view);
    }

    public GLSurfaceView createNewView() {
        GLSurfaceView view = new GLSurfaceView(this);
        view.setRenderer(new ShapeRenderer());
        view.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        view.setOnTouchListener(new OnSwipeTouchListener(this) {

            @Override
            public void onSwipeLeft() {
                if (viewListIndex > 0) {
                    viewListIndex--;
                    setContentView(viewList.get(viewListIndex));
                }
            }

            @Override
            public void onSwipeRight() {
                GLSurfaceView view;
                viewListIndex++;
                if (viewListIndex == viewList.size()) {
                    view = createNewView();
                    viewList.add(view);
                } else {
                    view = viewList.get(viewListIndex);
                }
                setContentView(view);
            }
        });
        return view;
    }

}
