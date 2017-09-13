package com.nserdyuk.smartkid;

import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.nserdyuk.smartkid.common.Complexity;
import com.nserdyuk.smartkid.tasks.ChatActivity;
import com.nserdyuk.smartkid.common.Constants;
import com.nserdyuk.smartkid.tasks.DictionaryActivity;
import com.nserdyuk.smartkid.tasks.ExaminationActivity;
import com.nserdyuk.smartkid.tasks.Grid2dActivity;
import com.nserdyuk.smartkid.tasks.TimeSetActivity;

import java.util.HashMap;
import java.util.Map;

public class GradeActivity extends AppCompatActivity {
    private int grade;
    private String[] arrayOfTasks;
    private Map<String, Intent> nameToIntentMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);
        grade = getIntent().getIntExtra(Constants.ATTRIBUTE_GRADE, 0);
        setupNameToIntent();
        initListView();
    }

    private void initListView() {
        TypedArray menuResources = getResources().obtainTypedArray(R.array.all_grades);
        int id = menuResources.getResourceId(grade, 0);
        arrayOfTasks = getResources().getStringArray(id);

        ListView lv = (ListView) findViewById(R.id.list_activity_grade_base);
        BaseAdapter adapter = new ArrayAdapter<>(this, R.layout.activity_main_list_item,
                R.id.activity_main_list_item_label, arrayOfTasks);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String name = arrayOfTasks[(int) id];
                Intent intent = nameToIntentMap.get(name);
                if (intent != null) {
                    intent.putExtra(Constants.ATTRIBUTE_TITLE, name);
                    startActivity(intent);
                }
            }
        });
        menuResources.recycle();
    }

    private void setupNameToIntent() {
        Intent intent;
        nameToIntentMap = new HashMap<>();

        intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, 1);
        intent.putExtra(Constants.ATTRIBUTE_FILE, "simple_add.txt");
        nameToIntentMap.put(getResources().getString(R.string.activity_grade_simple_add10), intent);

        intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, 1);
        intent.putExtra(Constants.ATTRIBUTE_FILE, "simple_guess.txt");
        nameToIntentMap.put(getResources().getString(R.string.activity_grade_guess_number), intent);

        intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, 1);
        intent.putExtra(Constants.ATTRIBUTE_FILE, "simple_sub.txt");
        nameToIntentMap.put(getResources().getString(R.string.activity_grade_simple_sub10), intent);

        intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, 1);
        intent.putExtra(Constants.ATTRIBUTE_FILE, "simple_add_20.txt");
        nameToIntentMap.put(getResources().getString(R.string.activity_grade_simple_add20), intent);

        intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, 1);
        intent.putExtra(Constants.ATTRIBUTE_FILE, "simple_sub_20.txt");
        nameToIntentMap.put(getResources().getString(R.string.activity_grade_simple_sub20), intent);

        intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, 3);
        intent.putExtra(Constants.ATTRIBUTE_FILE, "sub_zero.txt");
        nameToIntentMap.put(getResources().getString(R.string.activity_grade_sub_zero), intent);

        intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, 3);
        intent.putExtra(Constants.ATTRIBUTE_FILE, "sub_zero_long.txt");
        nameToIntentMap.put(getResources().getString(R.string.activity_grade_sub_zero_long), intent);

        intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, 3);
        intent.putExtra(Constants.ATTRIBUTE_FILE, "muldiv_10_100_1000.txt");
        nameToIntentMap.put(getResources().getString(R.string.activity_grade_muldiv_10_100_1000), intent);

        intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, 3);
        intent.putExtra(Constants.ATTRIBUTE_FILE, "single_quotient.txt");
        nameToIntentMap.put(getResources().getString(R.string.activity_grade_single_quotient), intent);

        intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, 3);
        intent.putExtra(Constants.ATTRIBUTE_FILE, "long_div.txt");
        nameToIntentMap.put(getResources().getString(R.string.activity_grade_long_div), intent);

        intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, 1);
        intent.putExtra(Constants.ATTRIBUTE_FILE, "long_long_div.txt");
        nameToIntentMap.put(getResources().getString(R.string.activity_grade_long_long_div), intent);

        intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, 1);
        intent.putExtra(Constants.ATTRIBUTE_FILE, "div_rem.txt");
        nameToIntentMap.put(getResources().getString(R.string.activity_grade_div_rem), intent);

        intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, 3);
        intent.putExtra(Constants.ATTRIBUTE_FILE, "long_mul.txt");
        nameToIntentMap.put(getResources().getString(R.string.activity_grade_long_mul), intent);

        intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, 1);
        intent.putExtra(Constants.ATTRIBUTE_FILE, "time_hard.txt");
        intent.putExtra(Constants.ATTRIBUTE_FILE_MASK, "TIME.jpg");
        nameToIntentMap.put(getResources().getString(R.string.activity_grade_time_add_sub_hard), intent);

        intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, 1);
        intent.putExtra(Constants.ATTRIBUTE_FILE, "time_easy_add.txt");
        intent.putExtra(Constants.ATTRIBUTE_FILE_MASK, "TIME.jpg");
        nameToIntentMap.put(getResources().getString(R.string.activity_grade_time_add_easy), intent);

        intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, 1);
        intent.putExtra(Constants.ATTRIBUTE_FILE, "time_easy_sub.txt");
        intent.putExtra(Constants.ATTRIBUTE_FILE_MASK, "TIME.jpg");
        nameToIntentMap.put(getResources().getString(R.string.activity_grade_time_sub_easy), intent);

        intent = new Intent(this, Grid2dActivity.class);
        intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, 1);
        intent.putExtra(Constants.ATTRIBUTE_COMPLEXITY, Complexity.EASY.toString());
        nameToIntentMap.put(getResources().getString(R.string.activity_grade_grid_2d_easy), intent);

        intent = new Intent(this, Grid2dActivity.class);
        intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, 7);
        intent.putExtra(Constants.ATTRIBUTE_COMPLEXITY, Complexity.HARD.toString());
        nameToIntentMap.put(getResources().getString(R.string.activity_grade_grid_2d_hard), intent);

        intent = new Intent(this, DictionaryActivity.class);
        intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, 12);
        intent.putExtra(Constants.ATTRIBUTE_FILE_MASK, "First250Words");
        intent.putExtra(Constants.ATTRIBUTE_MULTILANG, false);
        nameToIntentMap.put(getResources().getString(R.string.activity_grade_dictionary), intent);

        intent = new Intent(this, DictionaryActivity.class);
        intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, 12);
        intent.putExtra(Constants.ATTRIBUTE_FILE_MASK, "First250Words");
        intent.putExtra(Constants.ATTRIBUTE_MULTILANG, true);
        nameToIntentMap.put(getResources().getString(R.string.activity_grade_dictionary_multilang), intent);

        intent = new Intent(this, ExaminationActivity.class);
        intent.putExtra(Constants.ATTRIBUTE_FILE, "PresentSimple.txt");
        intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, 3);
        nameToIntentMap.put(getResources().getString(R.string.activity_grade_present_simple), intent);

        intent = new Intent(this, ExaminationActivity.class);
        intent.putExtra(Constants.ATTRIBUTE_FILE, "PresentContinuous.txt");
        intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, 3);
        nameToIntentMap.put(getResources().getString(R.string.activity_grade_present_continuous), intent);

        intent = new Intent(this, TimeSetActivity.class);
        intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, 10);
        intent.putExtra(Constants.ATTRIBUTE_COMPLEXITY, Complexity.EASY.toString());
        nameToIntentMap.put(getResources().getString(R.string.activity_grade_time_set_easy), intent);

        intent = new Intent(this, TimeSetActivity.class);
        intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, 10);
        intent.putExtra(Constants.ATTRIBUTE_COMPLEXITY, Complexity.HARD.toString());
        nameToIntentMap.put(getResources().getString(R.string.activity_grade_time_set_hard), intent);

        // TODO: add files all_english_Masha23.txt
    }
}
