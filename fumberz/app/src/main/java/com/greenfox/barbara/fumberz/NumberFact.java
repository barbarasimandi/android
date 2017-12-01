package com.greenfox.barbara.fumberz;

import com.google.gson.annotations.SerializedName;

/**
 * Created by barba on 29/11/2017.
 */

public class NumberFact {

    private String text;
    private Integer number;
    private Boolean found;

    @SerializedName("type")
    private String operation;

    public NumberFact(String text, Integer number, Boolean found, String operation) {
        this.text = text;
        this.number = number;
        this.found = found;
        this.operation = operation;
    }

    public NumberFact() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Boolean getFound() {
        return found;
    }

    public void setFound(Boolean found) {
        this.found = found;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}