package com.nserdyuk.smartkid.io;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class RandomReader {
    private int lines;
    private String fileName;
    private int examples;
    private AssetManager am;

    public RandomReader(AssetManager am, String fileName, int examples) {
        this.am = am;
        this.fileName = fileName;
        this.examples = examples;
    }

    public String[] loadRandomLines() throws IOException {
        if (lines == 0) {
            countLines();
        }

        final String ret[] = new String[examples];
        final int randomPosition = new Random().nextInt(lines - examples + 1);

        new AbstractReader() {
            public void doProcess(BufferedReader bufferedReader) throws IOException {
                int filePosition = 0;
                while (filePosition++ < randomPosition) {
                    bufferedReader.readLine();
                }

                int cnt = 0;
                while (cnt < examples) {
                    ret[cnt++] = bufferedReader.readLine();
                }
            }
        }.process(fileName);

        return ret;
    }

    private void countLines() throws IOException {
        new AbstractReader() {
            public void doProcess(BufferedReader bufferedReader) throws IOException {
                String str;
                lines = 0;
                while ((str = bufferedReader.readLine()) != null) {
                    if (str.trim().isEmpty()) {
                        throw new IOException("empty lines not allowed");
                    }
                    lines++;
                }
            }
        }.process(fileName);
    }

    private abstract class AbstractReader {
        public abstract void doProcess(BufferedReader bufferedReader) throws IOException;

        public void process(String fileName) throws IOException {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(am.open(fileName)))) {
                doProcess(br);
            }
        }
    }

}
