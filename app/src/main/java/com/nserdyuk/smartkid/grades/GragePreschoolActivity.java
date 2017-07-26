package com.nserdyuk.smartkid.grades;

import android.content.Intent;
import android.os.Bundle;

import com.nserdyuk.smartkid.R;
import com.nserdyuk.smartkid.tasks.ChatActivity;

import java.util.HashMap;
import java.util.Map;

public class GragePreschoolActivity extends GradeBaseActivity {
    private Map<String, Intent> nameToIntentMap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupNameToIntent();
        initListView(R.array.activity_main_preschool_tasks, nameToIntentMap);
    }

    private void setupNameToIntent() {
        Intent intent;
        nameToIntentMap = new HashMap<>();

        intent = new Intent(this, ChatActivity.class);
        nameToIntentMap.put(getResources().getString(R.string.activity_simple_add10), intent);
    }
}
