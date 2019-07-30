package com.eryanet.m85musicsdk.bean;

public class Album {
    int id;
    String title;
    int category_id;
    String cover_small;
    String cover_middle;
    String cover_large;

    int uid;
    String announcer;
    int isfinished;
    int include_track_count;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getCover_small() {
        return cover_small;
    }

    public void setCover_small(String cover_small) {
        this.cover_small = cover_small;
    }

    public String getCover_middle() {
        return cover_middle;
    }

    public void setCover_middle(String cover_middle) {
        this.cover_middle = cover_middle;
    }

    public String getCover_large() {
        return cover_large;
    }

    public void setCover_large(String cover_large) {
        this.cover_large = cover_large;
    }


    private int getUid() {
        return uid;
    }

    private void setUid(int uid) {
        this.uid = uid;
    }

    private String getAnnouncer() {
        return announcer;
    }

    private void setAnnouncer(String announcer) {
        this.announcer = announcer;
    }

    private int getIsfinished() {
        return isfinished;
    }

    private void setIsfinished(int isfinished) {
        this.isfinished = isfinished;
    }

    private int getInclude_track_count() {
        return include_track_count;
    }

    private void setInclude_track_count(int include_track_count) {
        this.include_track_count = include_track_count;
    }
}
