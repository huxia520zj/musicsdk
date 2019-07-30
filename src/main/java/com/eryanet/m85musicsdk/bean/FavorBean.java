package com.eryanet.m85musicsdk.bean;

import java.util.List;

public class FavorBean {
    boolean isSuccess;
    List<Favor> favorList;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public List<Favor> getFavorList() {
        return favorList;
    }

    public void setFavorList(List<Favor> favorList) {
        this.favorList = favorList;
    }
}
