package com.eryanet.m85musicsdk.bean;

import java.util.List;

public class RecommendBean {
    boolean isSuccess;
    List<Recommend> recommendList;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public List<Recommend> getRecommendList() {
        return recommendList;
    }

    public void setRecommendList(List<Recommend> recommendList) {
        this.recommendList = recommendList;
    }
}
