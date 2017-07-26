package com.nserdyuk.smartkid.grades;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nserdyuk.smartkid.R;

public class ListAdapter extends ArrayAdapter<String> {
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
