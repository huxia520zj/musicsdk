package com.eryanet.m85musicsdk.bean;

public class FavorResult {
    boolean isSuccess;
    String type;
    Favor favor;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Favor getFavor() {
        return favor;
    }

    public void setFavor(Favor favor) {
        this.favor = favor;
    }
}
