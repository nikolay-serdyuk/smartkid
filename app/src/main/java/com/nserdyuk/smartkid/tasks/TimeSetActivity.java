package com.nserdyuk.smartkid.tasks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.nserdyuk.smartkid.R;
import com.nserdyuk.smartkid.common.Complexity;
import com.nserdyuk.smartkid.common.Constants;
import com.nserdyuk.smartkid.common.Dialogs;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class TimeSetActivity extends AppCompatActivity {
    private final List<Pair<Integer, String>> HOURS = new ArrayList<>();
    private final List<Pair<Integer, String>> MINUTES = new ArrayList<>();
    private final List<Pair<Integer, String>> HOURS_FIRST_HALF = new ArrayList<>();
    private final List<Pair<Integer, String>> MINUTES_FIRST_HALF = new ArrayList<>();
    private final List<Pair<Integer, String>> HOURS_SECOND_HALF = new ArrayList<>();
    private final List<Pair<Integer, String>> MINUTES_SECOND_HALF = new ArrayList<>();
    private final Random RANDOM = new Random();

    private String rightAnswerMsg;
    private String wrongAnswerMsg;
    private long selectedItem = -1;
    private Example[] examples;

    private TimePickerDialog timePickerDialog;
    private BaseAdapter adapter;

    @Override
    public void onBackPressed() {
        Dialogs.showExitDialog(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeset);

        rightAnswerMsg = getResources().getString(R.string.rightAnswer);
        wrongAnswerMsg = getResources().getString(R.string.wrongAnswer);

        fillArray(getResources().getStringArray(R.array.activity_timeset_hours), HOURS);
        fillArray(getResources().getStringArray(R.array.activity_timeset_minutes), MINUTES);
        fillArray(getResources().getStringArray(R.array.activity_timeset_hours_first_half), HOURS_FIRST_HALF);
        fillArray(getResources().getStringArray(R.array.activity_timeset_minutes_first_half), MINUTES_FIRST_HALF);
        fillArray(getResources().getStringArray(R.array.activity_timeset_hours_second_half), HOURS_SECOND_HALF);
        fillArray(getResources().getStringArray(R.array.activity_timeset_minutes_second_half), MINUTES_SECOND_HALF);

        ExampleFactory[] factories = new ExampleFactory[]{
                new ExampleFactory(HOURS, MINUTES, false),
                new ExampleFactory(HOURS_FIRST_HALF, MINUTES_FIRST_HALF, true),
                new ExampleFactory(HOURS_SECOND_HALF, MINUTES_SECOND_HALF, true)
        };

        int examplesNum = getIntent().getIntExtra(Constants.ATTRIBUTE_EXAMPLES, 0);
        examples = new Example[examplesNum];
        Complexity complexity = Complexity.valueOf(getIntent().getStringExtra(Constants.ATTRIBUTE_COMPLEXITY));
        int range = complexity == Complexity.EASY ? 1 : factories.length;
        for (int i = 0; i < examplesNum; i++) {
            examples[i] = factories[RANDOM.nextInt(range)].create();
        }

        ListView lv = (ListView) findViewById(R.id.list_activity_timeset);
        adapter = new ArrayAdapter<>(this, R.layout.activity_timeset_list_item,
                R.id.activity_timeset_list_item_label, examples);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                selectedItem = id;
                timePickerDialog.show(getSupportFragmentManager(), "time");
            }
        });

        timePickerDialog = TimePickerDialog.newInstance(new OnTimeSetListener(), 11, 45, false, false);
    }

    private void fillArray(String[] items, List<Pair<Integer, String>> pairs) {
        for (String item : items) {
            String[] parts = StringUtils.split(item, Constants.STRING_DELIMITER);
            pairs.add(new ImmutablePair<>(Integer.parseInt(parts[0].trim()), parts[1].trim()));
        }
    }

    private static class TimeItem {
        private final int hours;
        private final int minutes;

        TimeItem(int hours, int minutes) {
            this.hours = hours;
            this.minutes = minutes;
        }

        @Override
        public String toString() {
            return String.format(Locale.US, Constants.TIME_ITEM_FORMAT, hours, minutes);
        }

        @Override
        public int hashCode() {
            return hours * 31 + minutes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof TimeItem)) {
                return false;
            }
            TimeItem to = (TimeItem) o;
            return hours == to.hours && minutes == to.minutes;
        }
    }

    private class Example {
        private final TimeItem timeItem;
        private final String expression;
        private TimeItem userInput;

        Example(TimeItem timeItem, String expression) {
            this.timeItem = timeItem;
            this.expression = expression;
        }

        void setUserInput(TimeItem userInput) {
            this.userInput = userInput;
        }

        @Override
        public String toString() {
            if (userInput == null) {
                return expression;
            }
            return String.format(Locale.US, Constants.TIMESET_EXAMPLE_FORMAT, expression, userInput.toString(), timeItem.equals(userInput) ? rightAnswerMsg : wrongAnswerMsg);
        }
    }

    private class ExampleFactory {
        private final Random RANDOM = new Random();
        private final List<Pair<Integer, String>> hoursMap;
        private final List<Pair<Integer, String>> minutesMap;
        private final boolean swapHoursMinutes;

        ExampleFactory(List<Pair<Integer, String>> hoursMap,
                       List<Pair<Integer, String>> minutesMap, boolean swapHoursMinutes) {
            this.hoursMap = hoursMap;
            this.minutesMap = minutesMap;
            this.swapHoursMinutes = swapHoursMinutes;
        }

        Example create() {
            int hoursIdx = RANDOM.nextInt(hoursMap.size());
            Pair<Integer, String> hoursPair = hoursMap.get(hoursIdx);
            String hoursStr = hoursPair.getRight();

            int minutesIdx = RANDOM.nextInt(minutesMap.size());
            Pair<Integer, String> minutesPair = minutesMap.get(minutesIdx);
            String minutesStr = minutesPair.getRight();

            String expression = swapHoursMinutes ? minutesStr + " " + hoursStr : hoursStr + " " + minutesStr;
            return new Example(new TimeItem(hoursPair.getLeft(), minutesPair.getLeft()), expression);
        }
    }

    private class OnTimeSetListener implements TimePickerDialog.OnTimeSetListener {

        @Override
        public void onTimeSet(RadialPickerLayout view, int hour, int minute) {
            int adjustedHour = hour % 12;
            examples[(int) selectedItem].setUserInput(new TimeItem(adjustedHour == 0 ? 12 : adjustedHour, minute));
            adapter.notifyDataSetChanged();
        }
    }
}