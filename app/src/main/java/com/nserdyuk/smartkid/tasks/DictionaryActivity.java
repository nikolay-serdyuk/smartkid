package com.nserdyuk.smartkid.tasks;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nserdyuk.smartkid.R;
import com.nserdyuk.smartkid.common.Constants;
import com.nserdyuk.smartkid.common.Dialogs;
import com.nserdyuk.smartkid.common.OnSwipeTouchListener;
import com.nserdyuk.smartkid.common.Utils;
import com.nserdyuk.smartkid.io.TextReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DictionaryActivity extends AppCompatActivity {
    private static final String TAG = DictionaryActivity.class.getName();
    private static final String ERROR_IO = "An I/O error occurred while reading a file";
    private static final String ERROR_NO_LINES = "File is empty";
    private static final String ERROR_NO_FILES = "No files found";
    private static final String EMPTY_STRING = "";

    private final List<String> files = Collections.synchronizedList(new ArrayList<String>());
    private final List<String> lines = Collections.synchronizedList(new ArrayList<String>());
    private int fileNumber;
    private int examplesNum;
    private boolean multilang;
    private ArrayAdapter<String> adapter;
    private TextView textView;

    @Override
    public void onBackPressed() {
        Dialogs.showExitDialog(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        textView = (TextView) findViewById(R.id.tv_activity_dictionary);
        adapter = new ArrayAdapter<>(this, R.layout.activity_dictionary_list_item,
                R.id.activity_dictionary_list_item_label, lines);
        ListView listView = (ListView) findViewById(R.id.list_activity_dictionary);
        listView.setAdapter(adapter);
        listView.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                new ReadRandomLinesTask(getAssets(), getPreviousFileName(), examplesNum)
                        .executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            }

            @Override
            public void onSwipeRight() {
                new ReadRandomLinesTask(getAssets(), getNextFileName(), examplesNum)
                        .executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            }
        });

        examplesNum = getIntent().getIntExtra(Constants.ATTRIBUTE_EXAMPLES, 3);
        String fileMask = getIntent().getStringExtra(Constants.ATTRIBUTE_FILE_MASK);

        multilang = getIntent().getBooleanExtra(Constants.ATTRIBUTE_MULTILANG, false);

        new GetFileListTask(getAssets(), fileMask).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private String getNextFileName() {
        fileNumber++;
        fileNumber %= files.size();
        return files.get(fileNumber);
    }

    private String getPreviousFileName() {
        fileNumber = files.size() + fileNumber - 1;
        fileNumber %= files.size();
        return files.get(fileNumber);
    }

    private class GetFileListTask extends AsyncTask<Void, Void, String> {
        private final String TAG = GetFileListTask.class.getName();
        private final AssetManager am;
        private final String fileMask;

        private GetFileListTask(AssetManager am, String fileMask) {
            this.am = am;
            this.fileMask = fileMask;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String[] list = am.list(EMPTY_STRING);
                for (String file : list) {
                    if (file.contains(fileMask)) {
                        files.add(file);
                    }
                }

                if (!files.isEmpty()) {
                    return loadLastViewedFile(files.get(0));
                }
            } catch (IOException e) {
                Log.e(TAG, ERROR_IO, e);
                Utils.showErrorInUiThread(DictionaryActivity.this, ERROR_IO);
            }
            return EMPTY_STRING;
        }

        @Override
        protected void onPostExecute(String lastViewedFile) {
            if (files.isEmpty()) {
                Utils.showError(DictionaryActivity.this, ERROR_NO_FILES);
            } else {
                int indexOfLast = files.indexOf(lastViewedFile);
                fileNumber = indexOfLast < 0 ? 0 : indexOfLast;
                new ReadRandomLinesTask(am, lastViewedFile, examplesNum)
                        .executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            }
        }

        private String loadLastViewedFile(String defaultFile) {
            SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            return preferences.getString(DictionaryActivity.TAG, defaultFile);
        }
    }

    private class ReadRandomLinesTask extends AsyncTask<Void, Void, Void> {
        private final String TAG = ReadRandomLinesTask.class.getName();
        private final AssetManager am;
        private final String fileName;
        private final int examplesNum;

        private ReadRandomLinesTask(AssetManager am, String fileName, int examplesNum) {
            this.am = am;
            this.fileName = fileName;
            this.examplesNum = examplesNum;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Random random = new Random();
                saveLastViewedFile(fileName);

                String[] newLines = new TextReader(am, fileName, examplesNum).readRandomLines();
                lines.clear();
                for (String newLine : newLines) {
                    String[] parts = newLine.split(Constants.STRING_DELIMITER);
                    lines.add(parts[multilang ? random.nextInt(parts.length) : 0]);
                }
            } catch (IOException e) {
                Log.e(TAG, ERROR_IO, e);
                Utils.showErrorInUiThread(DictionaryActivity.this, ERROR_IO);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (lines.isEmpty()) {
                Utils.showError(DictionaryActivity.this, ERROR_NO_LINES);
            } else {
                adapter.notifyDataSetChanged();
                textView.setText(files.get(fileNumber));
            }
        }

        private void saveLastViewedFile(String file) {
            SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(DictionaryActivity.TAG, file);
            editor.commit();
        }
    }
}