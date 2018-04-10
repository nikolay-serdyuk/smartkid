package com.nserdyuk.smartkid.models;

public class Answer {
    private String value;
    private String hint;

    public void setValue(String value) {
        this.value = value;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getValue() {
        return value;
    }

    public String getHint() {
        return hint;
    }
}
