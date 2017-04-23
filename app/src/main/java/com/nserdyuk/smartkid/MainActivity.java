package com.nserdyuk.smartkid;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView lv = (ListView) findViewById(R.id.list_activity_main);
        String[] grades = getResources().getStringArray(R.array.activity_main_grades);
        ListAdapter adapter = new ListAdapter(this, grades);
        lv.setAdapter(adapter);
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
}
