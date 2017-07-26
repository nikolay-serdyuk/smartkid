package com.nserdyuk.smartkid.grades;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nserdyuk.smartkid.R;

import java.util.Map;

public class GradeBaseActivity extends AppCompatActivity {
    private String[] arrayOfTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_base);
    }

    protected void initListView(int arrayId, final Map<String, Intent> nameToIntentMap) {
        ListView lv = (ListView) findViewById(R.id.list_activity_grade_base);
        arrayOfTasks = getResources().getStringArray(arrayId);
        ListAdapter adapter = new ListAdapter(this, arrayOfTasks);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String name = arrayOfTasks[(int) id];
                Intent intent = nameToIntentMap.get(name);
                if(intent != null) {
                    startActivity(intent);
                }
            }
        });
    }
}
