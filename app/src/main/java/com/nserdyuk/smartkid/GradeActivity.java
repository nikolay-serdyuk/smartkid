package com.nserdyuk.smartkid;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.nserdyuk.smartkid.common.Complexity;
import com.nserdyuk.smartkid.common.Utils;
import com.nserdyuk.smartkid.models.Assignment;
import com.nserdyuk.smartkid.common.Constants;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GradeActivity extends AppCompatActivity {
    private static final String SETUP_FILE = "assignments.json";
    private static final String ACTIVITY_NOT_FOUND_ERROR = "Activity not found";
    private static final String CANT_READ_SETUP_ERROR = "Can't read setup file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);
        int grade = getIntent().getIntExtra(Constants.ATTRIBUTE_GRADE, 0);
        try {
            List<NamedIntent> intentList = initIntentArray(grade);
            intentList.sort(Comparator.comparing(NamedIntent::getName));
            initListView(intentList);
        } catch (IOException e) {
            Utils.showError(this, CANT_READ_SETUP_ERROR);
        } catch (ClassNotFoundException e) {
            Utils.showError(this, ACTIVITY_NOT_FOUND_ERROR);
        }
    }

    private List<NamedIntent> initIntentArray(int grade) throws IOException, ClassNotFoundException {
        Gson gson = new Gson();
        List<NamedIntent> intentList = new ArrayList<>();
        AssetManager am = getAssets();
        try (InputStream is = am.open(SETUP_FILE)) {
            JsonArray array = new JsonParser().parse(new InputStreamReader(is)).getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                Assignment assignment = gson.fromJson(array.get(i), Assignment.class);
                if (ArrayUtils.contains(assignment.getGrades(), grade)) {
                    intentList.add(createIntent(assignment));
                }
            }
        }
        return intentList;
    }

    private void initListView(List<NamedIntent> intentArray) {
        ListView listView = (ListView) findViewById(R.id.list_activity_grade_base);
        NamedIntent[] array = intentArray.toArray(new NamedIntent[0]);
        listView.setAdapter(new Adapter(this, array));
        listView.setOnItemClickListener((parent, view, position, id1) -> {
            NamedIntent ni = (NamedIntent) listView.getItemAtPosition(position);
            startActivity(ni);
        });
    }

    private NamedIntent createIntent(Assignment assignment) throws ClassNotFoundException {
        NamedIntent intent;

        Class<?> clazz = Class.forName(assignment.getActivity());
        intent = new NamedIntent(this, clazz, assignment.getTitle());
        intent.putExtra(Constants.ATTRIBUTE_TITLE, assignment.getTitle());
        if (assignment.getExamples() != 0) {
            intent.putExtra(Constants.ATTRIBUTE_EXAMPLES, assignment.getExamples());
        }
        if (assignment.getComplexity() != null) {
            intent.putExtra(Constants.ATTRIBUTE_COMPLEXITY, Complexity.valueOf(assignment.getComplexity()));
        }
        if (assignment.getResource() != null) {
            intent.putExtra(Constants.ATTRIBUTE_FILE, assignment.getResource());
        }
        if (assignment.getFileMask() != null) {
            intent.putExtra(Constants.ATTRIBUTE_FILE_MASK, assignment.getFileMask());
        }
        intent.putExtra(Constants.ATTRIBUTE_FILE_MASK, assignment.getMultilang());
        return intent;
    }

    private static class Adapter extends ArrayAdapter<NamedIntent> {
        Adapter(@NonNull Context context, @NonNull NamedIntent[] objects) {
            super(context, R.layout.activity_main_list_item,
                    R.id.activity_main_list_item_label, objects);
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            NamedIntent intent = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.activity_main_list_item, parent, false);
            }
            TextView tvLabel = (TextView) convertView.findViewById(R.id.activity_main_list_item_label);
            tvLabel.setText(intent.getName());
            return convertView;
        }
    }

    private static class NamedIntent extends Intent {
        private final String name;

        NamedIntent(Context packageContext, Class<?> cls, String name) {
            super(packageContext, cls);
            this.name = name;
        }

        String getName() {
            return name;
        }
    }
}
