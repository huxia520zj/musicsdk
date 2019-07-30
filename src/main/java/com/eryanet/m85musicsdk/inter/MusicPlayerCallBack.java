package com.eryanet.m85musicsdk.inter;

import com.eryanet.m85musicsdk.bean.Track;

/**
 * Created by zhangjiao on 2018/7/18.
 *
 */

public interface MusicPlayerCallBack {
    public void onLoading();
    public void onPlayFail();
    public void onTrackInfo(Track track);
    public void onPlayOrPause(int state);
    public void onPlayMode(int mode);
    public void onProgress(long duration, long progress);
    public void onPlaySuccess();

}
