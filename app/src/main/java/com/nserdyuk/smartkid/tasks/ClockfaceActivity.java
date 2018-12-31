package com.nserdyuk.smartkid.tasks;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.nserdyuk.smartkid.R;
import com.nserdyuk.smartkid.common.Constants;
import com.nserdyuk.smartkid.common.Dialogs;
import com.nserdyuk.smartkid.common.Utils;
import com.nserdyuk.smartkid.io.ImageReader;
import com.nserdyuk.smartkid.views.ClockfaceView;

import java.util.Locale;
import java.util.Random;

public class ClockfaceActivity extends AppCompatActivity {
    private final static String ERROR_LOAD_CLOCKFACE_IMAGE = "An error occurred while loading the Clockface image";
    private final static String CLOCKFACE_PICTURE = "clockface.png";
    private final static int COLOR_BACKGROUND = Color.WHITE;
    private final static int LONG_DELAY = 1500;
    private final static int SHORT_DELAY = 800;

    private int solved;
    private int hour;
    private int minute;
    private String solvedMsg;
    private String rightAnswerMsg;
    private String wrongAnswerMsg;

    private TextView titleMessage;
    private ClockfaceView clockface;
    private TimePicker timePicker;

    private Handler handler;
    private Runnable lastCallback;

    @Override
    public void onBackPressed() {
        Dialogs.showExitDialog(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clockface);

        titleMessage = (TextView) findViewById(R.id.tv_activity_clockface);
        clockface = (ClockfaceView) findViewById(R.id.iv_activity_clockface);

        timePicker = (TimePicker) findViewById(R.id.tp_activity_clockface);
        timePicker.setEnabled(false);
        timePicker.setOnTimeChangedListener(new OnTimeChangedListener());

        setClockfaceImage();

        LinearLayout ll = (LinearLayout) findViewById(R.id.ll_activity_clockface);
        ll.setBackgroundColor(COLOR_BACKGROUND);

        String greetingMsg = getResources().getString(R.string.greeting);
        titleMessage.setText(greetingMsg);

        solvedMsg = getResources().getString(R.string.solved);
        rightAnswerMsg = getResources().getString(R.string.right_answer);
        wrongAnswerMsg = getResources().getString(R.string.wrong_answer);

        handler = new Handler();
        handler.postDelayed(new AskQuestion(true), SHORT_DELAY);
    }

    private void setClockfaceImage() {
        ImageReader ir = new ImageReader(getAssets()) {

            @Override
            protected void onPostExecute(Drawable drawable) {
                if (drawable != null) {
                    clockface.setImageDrawable(drawable);
                }
            }
        };
        ir.setOnErrorListener(e -> Utils.showErrorInUiThread(this, ERROR_LOAD_CLOCKFACE_IMAGE));
        ir.execute(CLOCKFACE_PICTURE);
    }

    private class AskQuestion implements Runnable {
        private final boolean askNew;

        AskQuestion(boolean askNew) {
            this.askNew = askNew;
        }

        public void run() {
            if (askNew) {
                Random random = new Random();
                hour = (random.nextInt(3) + 10) % 12;
                minute = random.nextInt(12) * 5;
                clockface.setHour(hour);
                clockface.setMinute(minute);
            }

            String title = solved != 0 ?
                    String.format(Locale.US, Constants.CLOCKFACE_TITLE_FORMAT, solvedMsg, solved) : "";
            titleMessage.setText(title);
            timePicker.setEnabled(true);
        }
    }

    private class CheckAnswer implements Runnable {
        private final int hour;
        private final int minute;

        CheckAnswer(int hour, int minute) {
            this.hour = hour;
            this.minute = minute;
        }

        public void run() {
            boolean askNew;

            timePicker.setEnabled(false);
            if (ClockfaceActivity.this.hour == this.hour && ClockfaceActivity.this.minute == this.minute) {
                titleMessage.setText(rightAnswerMsg);
                askNew = true;
                solved++;
            } else {
                titleMessage.setText(wrongAnswerMsg);
                askNew = false;
            }
            handler.postDelayed(new AskQuestion(askNew), SHORT_DELAY);
        }
    }

    private class OnTimeChangedListener implements TimePicker.OnTimeChangedListener {

        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            handler.removeCallbacks(lastCallback);
            lastCallback = new CheckAnswer(hourOfDay % 12, minute);
            handler.postDelayed(lastCallback, LONG_DELAY);
        }
    }
}
