package com.nserdyuk.smartkid.tasks;

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

public class DictionaryActivity extends AppCompatActivity {
    private static final String ERROR_IO = "An I/O error occurred while reading a file";
    private static final String ERROR_NO_LINES = "File is empty";
    private static final String ERROR_NO_FILES = "No files found";

    private final List<String> files = Collections.synchronizedList(new ArrayList<String>());
    private final List<String> lines = Collections.synchronizedList(new ArrayList<String>());
    private int fileNumber;
    private int examplesNum;
    private ArrayAdapter<String> adapter;
    private TextView textView;

    // TODO:
    // добавить имя файла внизу
    // сделать сохранение последнего файла
    // сделать английский наоборот
    // сделать подсветку стрелок влево вправо

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

    private class GetFileListTask extends AsyncTask<Void, Void, Void> {
        private final String TAG = GetFileListTask.class.getName();
        private final AssetManager am;
        private final String fileMask;

        private GetFileListTask(AssetManager am, String fileMask) {
            this.am = am;
            this.fileMask = fileMask;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String[] list = am.list("");
                for (String file : list) {
                    if (file.contains(fileMask)) {
                        files.add(file);
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, ERROR_IO, e);
                Utils.showErrorInUiThread(DictionaryActivity.this, ERROR_IO);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            if (files.isEmpty()) {
                Utils.showError(DictionaryActivity.this, ERROR_NO_FILES);
            } else {
                new ReadRandomLinesTask(am, files.get(0), examplesNum)
                        .executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            }
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
                String[] newLines = new TextReader(am, fileName, examplesNum).readRandomLines();
                lines.clear();
                for (String newLine : newLines) {
                    String[] parts = newLine.split(Constants.STRING_DELIMITER);
                    lines.add(parts[0]);
                }
            } catch (IOException e) {
                Log.e(TAG, ERROR_IO, e);
                Utils.showErrorInUiThread(DictionaryActivity.this, ERROR_IO);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(lines.isEmpty()) {
                Utils.showError(DictionaryActivity.this, ERROR_NO_LINES);
            } else {
                adapter.notifyDataSetChanged();
                textView.setText(files.get(fileNumber));
            }
        }
    }
}