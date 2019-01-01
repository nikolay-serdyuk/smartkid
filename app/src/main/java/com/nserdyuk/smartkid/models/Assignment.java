package com.nserdyuk.smartkid.models;

public class Assignment {
    private String title;
    private String activity;
    private int examples;
    private String complexity;
    private String resource;
    private String fileMask;
    private boolean multilang;
    private int grades[];

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public int getExamples() {
        return examples;
    }

    public void setExamples(int examples) {
        this.examples = examples;
    }

    public String getComplexity() {
        return complexity;
    }

    public void setComplexity(String complexity) {
        this.complexity = complexity;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getFileMask() {
        return fileMask;
    }

    public void setFileMask(String fileMask) {
        this.fileMask = fileMask;
    }

    public boolean getMultilang() {
        return multilang;
    }

    public void setMultilang(boolean multilang) {
        this.multilang = multilang;
    }

    public int[] getGrades() {
        return grades;
    }

    public void setGrades(int[] grades) {
        this.grades = grades;
    }
}
