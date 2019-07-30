package com.eryanet.m85musicsdk.sdk;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import com.eryanet.m85musicsdk.bean.AlbumBean;
import com.eryanet.m85musicsdk.bean.CategoryBean;
import com.eryanet.m85musicsdk.bean.FavorBean;
import com.eryanet.m85musicsdk.bean.FavorResult;
import com.eryanet.m85musicsdk.bean.RecommendBean;
import com.eryanet.m85musicsdk.bean.RequestResult;
import com.eryanet.m85musicsdk.bean.Track;
import com.eryanet.m85musicsdk.bean.TrackBean;
import com.eryanet.m85musicsdk.inter.IListResponse;
import com.eryanet.m85musicsdk.inter.IUpdateInfo;
import com.eryanet.m85musicsdk.inter.MusicListCallBack;
import com.eryanet.m85musicsdk.inter.MusicPlayerCallBack;
import com.eryanet.m85musicsdk.service.MusicPlayerService;
import com.eryanet.m85musicsdk.utils.LogUtils;
import com.eryanet.m85musicsdk.utils.RequestUtil;
import com.rich.czlylibary.sdk.MiguCzlySDK;
//import com.rich.czlylibary.sdk.MiguCzlySDK;

import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;

public class M85MusicSDK implements IListResponse, IUpdateInfo {
    private static final String TAG = "zhangjiao";
    private Context context;
    private static M85MusicSDK m85MusicSDK;
    private RequestUtil requestUtil;
    private MusicListCallBack musicListCallBack;
    private MusicPlayerCallBack musicPlayerCallBack;
    private MusicPlayerService.MusicBinder musicBinder;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.e("M85MusicSDK>>>onServiceDisconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.e("M85MusicSDK>>>onServiceConnected");
            musicBinder = (MusicPlayerService.MusicBinder) service;
            musicBinder.updateInfo(M85MusicSDK.this);

        }
    };

    public static M85MusicSDK getInstance(Context context) {
        if (m85MusicSDK == null) {
            synchronized (M85MusicSDK.class) {
                if (m85MusicSDK == null) {
                    m85MusicSDK = new M85MusicSDK(context);
                }
            }
        }
        return m85MusicSDK;
    }

    private M85MusicSDK(Context context) {
        this.context = context;
        requestUtil = new RequestUtil(context);
        requestUtil.setiListResponse(this);
    }

    public void setMusicListCallBack(MusicListCallBack musicListCallBack) {
        Log.e(TAG, "M85MusicSDK>>>setMusicListCallBack");
        this.musicListCallBack = musicListCallBack;
    }

    public void setMusicPlayerCallBack(MusicPlayerCallBack musicPlayerCallBack) {
        this.musicPlayerCallBack = musicPlayerCallBack;
    }

    public void init(Application application) {
        if (requestUtil != null) {
            if (requestUtil.getSource() == 0) {
                MiguCzlySDK.getInstance().init(application)
                        .setSmartDeviceId("f079e83193c1")//设置设备号（必填）
                        .setUid("111111")//设置用户ID（非必填）
                        .setKey("0eb5592e029d3621")//设置渠道号（必填）(为加密渠道号)
                        .setPhoneNum("11111111111");//设置手机号（非必填）

            } else if (requestUtil.getSource() == 1) {

            }
        }

    }

    public void initMusicPlayer() {
        LogUtils.e("M85MusicSDK>>>initMusicPlayer");
        Intent intent = new Intent(context, MusicPlayerService.class);
        context.bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    public void releaseMusicPlayer() {
        LogUtils.e("M85MusicSDK>>>releaseMusicPlayer");
        context.unbindService(serviceConnection);
        if(requestUtil != null) {
            requestUtil.releaseCloudService();
        }
    }
    /*private void initMusic(Application application) {
        String processName = getProcessName(context);
        Log.e(TAG, "processName = " + processName);
        if (processName != null) {
            if (context.getPackageName().equals(processName)) {
                MiguCzlySDK.getInstance().init(application)
                        .setSmartDeviceId("f079e83193c1")//设置设备号（必填）
                        .setUid("111111")//设置用户ID（非必填）
                        .setKey("0eb5592e029d3621")//设置渠道号（必填）(为加密渠道号)
                        .setPhoneNum("11111111111");//设置手机号（非必填）
            }
        }
    }*/

   /* private String getProcessName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
            if (proInfo.pid == android.os.Process.myPid()) {
                if (proInfo.processName != null) {
                    return proInfo.processName;
                }
            }

        }
        return null;
    }*/

    /**
     * 获取推荐列表  服务器接口 咪咕 喜马拉雅通用
     */
    public void requestRecommendList() {
        if (requestUtil != null) {
            requestUtil.requestRecommendList();
        }

    }

    /**
     * 获取分类列表  咪咕 喜马拉雅通用
     */
    public void requestCategoryList() {
        if (requestUtil != null) {
            requestUtil.requestCategoryList();
        }
    }

    /**
     * 获取标签列表下的歌曲
     */
    public void requestTagDetail(String tagID, int offset, int limit) {
        if (requestUtil != null) {
            if (requestUtil.sourceId == 0) {
                //用名字搜索 暂时写为id
                requestUtil.searchMusicByKey(tagID, offset, limit);
            } else if (requestUtil.sourceId == 1) {
                requestUtil.requestTagDetail(tagID, offset, limit);
            }

        }
    }

    /**
     * 获取专辑下歌曲列表
     */
    public void requestTrackList(int albumID, int offset, int limit) {
        if (requestUtil != null) {
            requestUtil.requestTrackList(albumID, offset, limit);
        }

    }

    /**
     * 曲目搜索
     */
    public void requestSearchTrackList(String keywords, int offset, int limit) {
        if (requestUtil != null) {
            if(requestUtil.sourceId == 0){

            }else if(requestUtil.sourceId == 1){
                requestUtil.requestSearchTrackList(keywords, offset, limit);
            }

        }
    }

    /**
     * 添加收藏
     */
    public void addFavor(Track track) {
        if (requestUtil != null) {
            requestUtil.addFavor(track);
        }
    }

    /**
     * 删除收藏
     */
    public void deleteFavor(int favorID) {
        if (requestUtil != null) {
            requestUtil.deleteFavor(favorID);
        }
    }

    /**
     * 获取收藏列表
     */
    public void requestFavorList() {
        if (requestUtil != null) {
            requestUtil.requestFavorList();
        }
    }

    public void playList(final List<Track> mTrackList, final int position) {
        if (musicBinder != null) {
            musicBinder.startPlay(mTrackList, position);
        }
    }

    /**
     * 播放
     */
    public void play() {
        if (musicBinder != null) {
            musicBinder.start();
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        if (musicBinder != null) {
            musicBinder.stop();
        }
    }

    public void stop() {
        if (musicBinder != null) {
            musicBinder.stopPlayer();
        }
    }

    /**
     * 释放
     */
    public void release() {
        if (musicBinder != null) {
            musicBinder.toRelease();
        }
    }


    public boolean isPlaying() {
        boolean result = false;
        if (musicBinder != null) {
            result = musicBinder.isPlaying();
        }
        return result;
    }

    /**
     * 播放上一曲
     */
    public void playPre() {
        if (musicBinder != null) {
            musicBinder.toPrevious();
        }
    }

    /**
     * 播放下一曲
     */
    public void playNext() {
        if (musicBinder != null) {
            musicBinder.toNext();
        }
    }

    /**
     * 设置播放模式
     */
    public void setPlayMode(int mode) {
        if (musicBinder != null) {
            musicBinder.setMode(mode);
        }
    }


    /**
     * 获取当前播放位置
     */
    public int getCurPos() {
        int pos = 0;
        if (musicBinder != null) {
            pos = musicBinder.getCurPosition();
        }
        return pos;
    }


    @Override
    public void responseRecommendList(RecommendBean recommendBean) {
        if (musicListCallBack != null) {
            musicListCallBack.responseRecommendList(recommendBean);
        }

    }

    @Override
    public void responseCategoryList(CategoryBean categoryBean) {
        if (musicListCallBack != null) {
            musicListCallBack.responseCategoryList(categoryBean);
        }
    }

    @Override
    public void responseTrackList(TrackBean trackBean) {
        if (musicListCallBack != null) {
            musicListCallBack.responseTrackList(trackBean);
        }
    }

    @Override
    public void responseFavorList(FavorBean favorBean) {
        if (musicListCallBack != null) {
            musicListCallBack.responseFavorList(favorBean);
        }
    }

    @Override
    public void responseFavorResult(FavorResult favorResult) {
        if (musicListCallBack != null) {
            musicListCallBack.responseFavorResult(favorResult);
        }
    }

 /*   @Override
    public void responseList(RequestResult<List<Track>> requestResult) {
        List<Track> trackList = requestResult.getT();
    }*/

    @Override
    public void onLoading() {
        if (musicPlayerCallBack != null) {
            musicPlayerCallBack.onLoading();
        }
    }

    @Override
    public void onPlayFail() {
        if (musicPlayerCallBack != null) {
            musicPlayerCallBack.onPlayFail();
        }
    }

    @Override
    public void onTrackInfo(Track track) {
        if (musicPlayerCallBack != null) {
            musicPlayerCallBack.onTrackInfo(track);
        }
    }

    @Override
    public void onPlayOrPause(int state) {
        if (musicPlayerCallBack != null) {
            musicPlayerCallBack.onPlayOrPause(state);
        }
    }

    @Override
    public void onPlayMode(int mode) {
        if (musicPlayerCallBack != null) {
            musicPlayerCallBack.onPlayMode(mode);
        }
    }

    @Override
    public void onProgress(long duration, long progress) {
        if (musicPlayerCallBack != null) {
            musicPlayerCallBack.onProgress(duration, progress);
        }
    }

    @Override
    public void onPlaySuccess() {
        if (musicPlayerCallBack != null) {
            musicPlayerCallBack.onPlaySuccess();
        }
    }
}
