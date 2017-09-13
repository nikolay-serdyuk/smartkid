package com.nserdyuk.smartkid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.nserdyuk.smartkid.common.Constants;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lv = (ListView) findViewById(R.id.list_activity_main);
        String[] grades = getResources().getStringArray(R.array.activity_main_grades);
        BaseAdapter adapter = new ArrayAdapter<>(this, R.layout.activity_main_list_item,
                R.id.activity_main_list_item_label, grades);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(MainActivity.this, GradeActivity.class);
                intent.putExtra(Constants.ATTRIBUTE_GRADE, (int) id);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}