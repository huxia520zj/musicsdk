package com.eryanet.m85musicsdk.bean;

import java.util.List;

public class CategoryBean {
    boolean isSuccess;
    List<Category> categoryList;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }
}
