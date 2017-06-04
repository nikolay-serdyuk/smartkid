package com.nserdyuk.smartkid;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nserdyuk.smartkid.grades.GragePreschoolActivity;
import com.nserdyuk.smartkid.tasks.QAChatActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Map<String, Intent> nameToIntentMap = null;
    private String[] arrayOfGrades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arrayOfGrades = getResources().getStringArray(R.array.activity_main_grades);
        setupNameToIntent();

        ListView lv = (ListView) findViewById(R.id.list_activity_main);
        String[] grades = getResources().getStringArray(R.array.activity_main_grades);
        ListAdapter adapter = new ListAdapter(this, grades);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String name = arrayOfGrades[(int) id];
                Intent intent = nameToIntentMap.get(name);
                intent = new Intent(MainActivity.this, QAChatActivity.class);
                if(intent != null) {
                    startActivity(intent);
                }
            }
        });
    }

    private static class ListAdapter extends ArrayAdapter<String> {
        public ListAdapter(Context context, String[] tasks) {
            super(context, 0, tasks);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            String label = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_main_list_item, parent, false);
            }
            TextView tvLabel = (TextView) convertView.findViewById(R.id.activity_main_list_item_label);
            tvLabel.setText(label);
            return convertView;
        }
    }

    private void setupNameToIntent() {
        Intent intent;
        nameToIntentMap = new HashMap<>();

        intent = new Intent(this, GragePreschoolActivity.class);
        nameToIntentMap.put(getResources().getString(R.string.activity_main_preschool), intent);
    }

    @Override
    public void onBackPressed() {
    }
}