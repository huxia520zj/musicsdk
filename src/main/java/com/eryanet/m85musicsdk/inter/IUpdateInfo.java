package com.eryanet.m85musicsdk.inter;


import com.eryanet.m85musicsdk.bean.Track;

/**
 * Created by zhangjiao on 2018/7/19.
 * 推送正在播放歌曲信息及播放状态
 */

public interface IUpdateInfo {
    public void onLoading();
    public void onPlayFail();
    public void onTrackInfo(Track track);
    public void onPlayOrPause(int state);
    public void onPlayMode(int mode);
    public void onProgress(long duration, long progress);
    public void onPlaySuccess();
}
