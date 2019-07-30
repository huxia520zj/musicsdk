
package com.eryanet.m85musicsdk.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaButtonReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.eryanet.m85musicsdk.bean.Track;
import com.eryanet.m85musicsdk.constants.CommonConstants;
import com.eryanet.m85musicsdk.inter.IUpdateInfo;
import com.eryanet.m85musicsdk.utils.AesUtil;
import com.eryanet.m85musicsdk.utils.LogUtils;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.DefaultMediaSourceEventListener;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.DefaultHlsDataSourceFactory;
import com.google.android.exoplayer2.source.hls.HlsDataSourceFactory;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.util.List;
import java.util.Random;

/**
 * Created by zhangjiao on 2018/7/9.
 */


public class MusicPlayerService extends Service {
    private ExtractorMediaSource extractorMediaSource;
    private ConcatenatingMediaSource concatenatingMediaSource;
    private DefaultDataSourceFactory defaultDataSourceFactory;
    private SimpleExoPlayer simpleExoPlayer;
    private AudioManager mAudioManager;
    private Binder musicBinder = new MusicBinder();
    public IUpdateInfo iUpdateInfo;
    //播放列表
    private List<Track> mTrackList;
    private Track mTrack;
    //当前播放位置
    int curPosition = 0;
    //是否播放完成
    private boolean isFinish = false;
    //计时器10s加载失败
    private CountDownTimer countDownTimer;
    //是否播放成功
    private boolean isPlaySuccess = false;
    //是否为失去音源焦点导致暂停
    private boolean isAudioLossPause = false;
    //是否正在播放
    private boolean isPlaying = false;
    //当前播放模式 0顺序播放 1随机播放 2单曲循环
    private int playMode = 0;


    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e("MusicPlayerService>>>onCreate");
        initExoPlayer();
        initAudioManager();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e("MusicPlayerService>>>onDestroy");
        release();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void initExoPlayer() {
        LogUtils.e("MusicPlayerService>>>initPlayer");
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        defaultDataSourceFactory = new DefaultDataSourceFactory(this, "audio/mpeg");
        concatenatingMediaSource = new ConcatenatingMediaSource();
        concatenatingMediaSource.addEventListener(handler, new DefaultMediaSourceEventListener() {
            @Override
            public void onLoadStarted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
                super.onLoadStarted(windowIndex, mediaPeriodId, loadEventInfo, mediaLoadData);
                handler.sendEmptyMessage(0);

            }

            @Override
            public void onLoadCompleted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
                super.onLoadCompleted(windowIndex, mediaPeriodId, loadEventInfo, mediaLoadData);
            }


        });

    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            long curProgress = 0;
            long duration = 0;
            if (simpleExoPlayer != null) {
                curProgress = simpleExoPlayer.getCurrentPosition();
                duration = simpleExoPlayer.getDuration();

            }
            if (curProgress > 0 && duration > 0 && iUpdateInfo != null && isPlaying() && !isFinish) {
                iUpdateInfo.onProgress(duration, curProgress);
            }
            if (curProgress > 0 && !isPlaySuccess) {
                if (iUpdateInfo != null) {
                    updateData();
                    iUpdateInfo.onPlayOrPause(0);
                    iUpdateInfo.onPlaySuccess();
                }
                isPlaying = true;
                isPlaySuccess = true;
                isFinish = false;
                countDownTimer.cancel();

            }
            if (simpleExoPlayer != null && simpleExoPlayer.getPlaybackState() == Player.STATE_ENDED) {
                isFinish = true;
                playAutoNext();
            }

            sendEmptyMessageDelayed(0, 1000);
        }

    };

    private void initAudioManager() {
        LogUtils.e("MusicPlayerService>>>initAudioManager");
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    private void setError() {
        isPlaySuccess = false;
        countDownTimer = new CountDownTimer(12 * 1000, 12 * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if (!isPlaySuccess) {
                    if (iUpdateInfo != null) {
                        iUpdateInfo.onPlayFail();
                    }
                }


            }
        };
        countDownTimer.start();

    }

    private void updateData() {
        LogUtils.e("MusicPlayerService>>>updateData");
        if (iUpdateInfo != null) {
            iUpdateInfo.onTrackInfo(mTrack);
        }
    }

    private void play(List<Track> trackList, int pos) {
        requestFocus();
        mTrackList = trackList;
        curPosition = pos;
        mTrack = mTrackList.get(curPosition);
        if (concatenatingMediaSource != null) {
            concatenatingMediaSource.clear();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        setError();
        if (iUpdateInfo != null) {
            iUpdateInfo.onLoading();
        }
        SystemClock.sleep(100);
        Log.e("zhangzimo","before url = "+mTrackList.get(pos).getPlay_url());
        String url = AesUtil.decrypt(CommonConstants.KEY,mTrackList.get(pos).getPlay_url());
        Log.e("zhangzimo","after url = "+url);
        if (!TextUtils.isEmpty(url)) {
            try {
                if (mTrackList != null && mTrackList.size() > 0) {
                    extractorMediaSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory)
                            .createMediaSource(Uri.parse(url));
                    updateData();
                }
                if (concatenatingMediaSource != null) {
                    concatenatingMediaSource.addMediaSource(extractorMediaSource);
                }
                if (simpleExoPlayer != null) {
                    simpleExoPlayer.setPlayWhenReady(true);
                    simpleExoPlayer.prepare(concatenatingMediaSource);
                }
            } catch (Exception e) {

            }
        } else {

        }


    }

    public void reloadPlay() {
        requestFocus();
        if (concatenatingMediaSource != null) {
            concatenatingMediaSource.clear();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        setError();
        if (iUpdateInfo != null) {
            iUpdateInfo.onLoading();
        }
        SystemClock.sleep(100);
        try {
            if (mTrackList != null && mTrackList.size() > 0) {
                Log.e("zhangzimo","before url = "+mTrackList.get(curPosition).getPlay_url());
                String url = AesUtil.decrypt(CommonConstants.KEY,mTrackList.get(curPosition).getPlay_url());
                Log.e("zhangzimo","after url = "+url);
                extractorMediaSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory).createMediaSource(Uri.parse(url));
                updateData();
            }
            if (concatenatingMediaSource != null) {
                concatenatingMediaSource.addMediaSource(extractorMediaSource);
            }
            if (simpleExoPlayer != null) {
                simpleExoPlayer.setPlayWhenReady(true);
                simpleExoPlayer.prepare(concatenatingMediaSource);
            }
        } catch (Exception e) {
        }
    }

    private void begin() {
        if (isPause() && simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(true);
        }
        if (iUpdateInfo != null) {
            isPlaying = true;
            iUpdateInfo.onPlayOrPause(0);

        }
    }

    private void pause() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
        }
        if (iUpdateInfo != null) {
            isPlaying = false;
            iUpdateInfo.onPlayOrPause(1);
        }
    }

    private void release() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.stop();
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
        abandonFocus();
    }

    private void playAutoNext() {
        setError();
        if (iUpdateInfo != null) {
            iUpdateInfo.onLoading();
        }
        if (playMode == 0) {
            if (mTrackList != null && mTrackList.size() > 0) {
                curPosition = (curPosition + 1) % mTrackList.size();
                play(mTrackList, curPosition % mTrackList.size());
            }
        } else if (playMode == 1) {
            if (mTrackList != null && mTrackList.size() > 0) {
                Random random = new Random();
                curPosition = random.nextInt(mTrackList.size());
                play(mTrackList, curPosition % mTrackList.size());
            }
        } else if (playMode == 2) {
            if (mTrackList != null && mTrackList.size() > 0) {
//                curPosition = (curPosition + 1) % mTrackList.size();
                play(mTrackList, curPosition % mTrackList.size());
            }
        }
    }

    private void playNext() {
        setError();
        if (iUpdateInfo != null) {
            iUpdateInfo.onLoading();
        }
        if (playMode == 0) {
            if (mTrackList != null && mTrackList.size() > 0) {
                curPosition = (curPosition + 1) % mTrackList.size();
                play(mTrackList, curPosition % mTrackList.size());
            }
        } else if (playMode == 1) {
            if (mTrackList != null && mTrackList.size() > 0) {
                Random random = new Random();
                curPosition = random.nextInt(mTrackList.size());
                play(mTrackList, curPosition % mTrackList.size());
            }
        } else if (playMode == 2) {
            if (mTrackList != null && mTrackList.size() > 0) {
                curPosition = (curPosition + 1) % mTrackList.size();
                play(mTrackList, curPosition % mTrackList.size());
            }
        }
    }

    private void playPrevious() {
        LogUtils.e("MusicPlayerService>>>playPrevious");
        if (iUpdateInfo != null) {
            iUpdateInfo.onLoading();
        }
        setError();
        if(playMode == 0) {
            curPosition--;
            if (mTrackList != null && mTrackList.size() > 0) {
                if (curPosition == -1) {
                    curPosition = mTrackList.size() - 1;
                }
                play(mTrackList, curPosition % mTrackList.size());
            }
        }else if(playMode == 1){
            if (mTrackList != null && mTrackList.size() > 0) {
                Random random = new Random();
                curPosition = random.nextInt(mTrackList.size());
                play(mTrackList, curPosition % mTrackList.size());
            }
        }else if(playMode == 2) {
            curPosition--;
            if (mTrackList != null && mTrackList.size() > 0) {
                if (curPosition == -1) {
                    curPosition = mTrackList.size() - 1;
                }
                play(mTrackList, curPosition % mTrackList.size());
            }
        }

    }

    private boolean isPlaying() {
        return simpleExoPlayer != null
                && simpleExoPlayer.getPlaybackState() != Player.STATE_ENDED
                && simpleExoPlayer.getPlaybackState() != Player.STATE_IDLE
                && simpleExoPlayer.getPlayWhenReady();
    }

    private void setPlayMode(int mode) {
        playMode = mode;
        iUpdateInfo.onPlayMode(mode);
    }

    private boolean isPause() {
        return simpleExoPlayer != null
                && simpleExoPlayer.getPlaybackState() != Player.STATE_ENDED
                && simpleExoPlayer.getPlaybackState() != Player.STATE_IDLE
                && !simpleExoPlayer.getPlayWhenReady();
    }

    private void setUpdateInfo(IUpdateInfo iUpdateInfo) {
        LogUtils.e("RadioPlayerService>>>setUpdateInfo");
        this.iUpdateInfo = iUpdateInfo;
    }

    private void stopMusicPlayer() {
        LogUtils.e("RadioPlayerService>>>setUpdateInfo");
        if (simpleExoPlayer != null) {
            simpleExoPlayer.stop();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }

    public class MusicBinder extends Binder {
        public void startPlay(List<Track> playList, int pos) {
            play(playList, pos);
        }

        public void start() {
            begin();
        }

        public void stop() {
            pause();
        }

        public void toRelease() {
            release();
        }

        public void stopPlayer() {
            stopMusicPlayer();
        }

        public void toNext() {
            playNext();
        }

        public void toPrevious() {
            playPrevious();
        }

        public void setMode(int mode) {
            setPlayMode(mode);
        }

        public boolean isPlaying() {
            return isPlaying;
        }

        public int getCurPosition() {
            return curPosition;
        }

        public void updateInfo(IUpdateInfo iUpdateInfo) {
            setUpdateInfo(iUpdateInfo);
        }

    }

    private boolean requestFocus() {
        LogUtils.e("MusicPlayerService>>>requestFocus");
        int result = mAudioManager.requestAudioFocus(mFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;

    }

    public boolean abandonFocus() {
        LogUtils.e("MusicPlayerService>>>abandonFocus");
        int result = mAudioManager.abandonAudioFocus(mFocusChangeListener);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }


    private AudioManager.OnAudioFocusChangeListener mFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    LogUtils.e(">>>AUDIOFOCUS_LOSS");
                    if (isPlaying()) {
                        pause();
                        isAudioLossPause = true;
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    LogUtils.e(">>>AUDIOFOCUS_LOSS_TRANSIENT");
                    if (isPlaying()) {
                        pause();
                        isAudioLossPause = true;
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    LogUtils.e(">>>AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                    if (isPlaying()) {
                        pause();
                        isAudioLossPause = true;
                    }

                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    LogUtils.e(">>>AUDIOFOCUS_GAIN");
                    if (isAudioLossPause) {
                        begin();
                        isAudioLossPause = false;
                    }
                    break;
            }
        }
    };
}

