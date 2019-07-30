package com.eryanet.m85musicsdk.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.eryanet.m85.cloudservice.CloudServiceImpl;
import com.eryanet.m85.cloudservice.bean.Loc;
import com.eryanet.m85.cloudservice.bean.Req;
import com.eryanet.m85.cloudservice.bean.Vin;
import com.eryanet.m85.cloudservice.util.GsonUtil;
import com.eryanet.m85musicsdk.bean.Album;
import com.eryanet.m85musicsdk.bean.Announcer;
import com.eryanet.m85musicsdk.bean.CategoryBean;
import com.eryanet.m85musicsdk.bean.Category;
import com.eryanet.m85musicsdk.bean.Favor;
import com.eryanet.m85musicsdk.bean.FavorBean;
import com.eryanet.m85musicsdk.bean.FavorResult;
import com.eryanet.m85musicsdk.bean.Recommend;
import com.eryanet.m85musicsdk.bean.RecommendBean;
import com.eryanet.m85musicsdk.bean.Tag;
import com.eryanet.m85musicsdk.bean.Track;
import com.eryanet.m85musicsdk.bean.TrackBean;
import com.eryanet.m85musicsdk.constants.CommonConstants;
import com.eryanet.m85musicsdk.constants.NetWorkUrLConstants;
import com.eryanet.m85musicsdk.inter.IHttpCallback;
import com.eryanet.m85musicsdk.inter.IListResponse;
import com.eryanet.m85musicsdk.tools.RequestNet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rich.czlylibary.bean.MusicInfo;
import com.rich.czlylibary.bean.MusicinfoResult;
import com.rich.czlylibary.bean.SearchSongNewResult;
import com.rich.czlylibary.bean.SearchTagSongNewResult;
import com.rich.czlylibary.sdk.HttpClientManager;
import com.rich.czlylibary.sdk.ResultCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangjiao on 2018/7/25.
 */

public class RequestUtil {
    private static final String TAG = "zhangjiao";
    private IListResponse iListResponse;
    private String string;
    private Context context;
    private List<Favor> mFavorList = new ArrayList<>();
    int favor_id = 0;
    //0咪咕 1喜马拉雅
    public int sourceId = 1;
    private static final String app = "music";


    public RequestUtil(Context context) {
        this.context = context;
        initCloudService();
//        loginVIN();
        try {
            CommonConstants.KEY = AesUtil.generateDesKey(256);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initCloudService() {
        cloudService = new CloudServiceImpl(context, app, ipc);
        cloudService.bindService();
    }

    public void releaseCloudService() {
        cloudService.unbindService();
    }

    public void refresh() {
        cloudService.request(GsonUtil.getGsonInstance().toJson(new Req<>(app, "refresh", null)));
    }

    public void setiListResponse(IListResponse iListResponse) {
        this.iListResponse = iListResponse;
    }

    public int getSource() {
        return sourceId;
    }
    /**
     * vin注册
     */
    /**
     * VIN登陆
     */
   /* public void loginVIN() {
        RequestNet.register(new IHttpCallback() {
            @Override
            public void onFailure(int errorCode, String errorMessage) {
                loginVIN();
            }

            @Override
            public void onResponse(String data, String finalUrl) throws IOException {
                try {
                    JSONObject object = new JSONObject(data);
                    CommonConstants.USER_ID = object.getString("userID");
                    CommonConstants.TOKEN = object.getString("token");
                    Log.e(TAG, "USER_ID " + CommonConstants.USER_ID);
                    Log.e(TAG, "TOKEN " + CommonConstants.TOKEN);
                    requestFavorList();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }*/

    /**
     * 刷新token
     */
    /*public void updateToken() {
        RequestNet.updateToken();
    }*/

    /**
     * 获取推荐专辑列表
     */
    public void requestRecommendList() {
        String finalUrl = NetWorkUrLConstants.RECOMMENDLIST_URL;
        RequestNet.baseGetRequest(finalUrl, new IHttpCallback() {
            RecommendBean recommendBean = new RecommendBean();

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                if (errorCode == 512) {
                    refresh();
                } else {
                    recommendBean.setSuccess(false);
                    iListResponse.responseRecommendList(recommendBean);
                }
            }

            @Override
            public void onResponse(String data, String finalUrl) throws IOException {
                try {
                    Gson gson = new Gson();
                    List<Recommend> list = gson.fromJson(data, new TypeToken<List<Recommend>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        recommendBean.setSuccess(true);
                        recommendBean.setRecommendList(list);
                        iListResponse.responseRecommendList(recommendBean);
                    } else {
                        recommendBean.setSuccess(false);
                        iListResponse.responseRecommendList(recommendBean);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    recommendBean.setSuccess(false);
                    iListResponse.responseRecommendList(recommendBean);
                }

            }
        });

    }

    /**
     * 获取分类列表
     */
    public void requestCategoryList() {
        String finalUrl = NetWorkUrLConstants.CATEGORYLIST_URL;
        RequestNet.baseGetRequest(finalUrl, new IHttpCallback() {
            CategoryBean categoryBean = new CategoryBean();

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                if (errorCode == 512) {
                    refresh();
                } else {
                    categoryBean.setSuccess(false);
                    iListResponse.responseCategoryList(categoryBean);
                }
            }

            @Override
            public void onResponse(String data, String finalUrl) throws IOException {
                try {
                    JSONArray jsonArray = new JSONArray(data);

                    List<Category> categoryList = new ArrayList<>();
                    if (categoryList != null && categoryList.size() > 0) {
                        categoryBean.setSuccess(true);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Category category = new Category();
                            category.setId(jsonObject.optInt("id"));
                            category.setName(jsonObject.optString("name"));
                            category.setCoverUrl(jsonObject.optString("cover_url"));
                            String tags = jsonObject.optString("tags");
                            Gson gson = new Gson();
                            List<Tag> list = gson.fromJson(tags, new TypeToken<List<Tag>>() {
                            }.getType());
                            category.setTagList(list);
                            categoryList.add(category);
                        }
                        categoryBean.setCategoryList(categoryList);
                        iListResponse.responseCategoryList(categoryBean);
                    } else {
                        categoryBean.setSuccess(false);
                        iListResponse.responseCategoryList(categoryBean);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    categoryBean.setSuccess(false);
                    iListResponse.responseCategoryList(categoryBean);
                }

            }
        });

    }

    /**
     * 获取标签下歌曲列表
     */
    public void requestTagDetail(String tagID, int offset, final int limit) {
        String finalUrl = NetWorkUrLConstants.TRACKLIST_TAG_URL + "?tagID=" + tagID + "&sort=asc" + "&offset=" + offset + "&limit=" + limit;
        RequestNet.baseGetRequest(finalUrl, new IHttpCallback() {
            TrackBean trackBean = new TrackBean();

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                if (errorCode == 512) {
                    refresh();
                } else {
                    trackBean.setSuccess(false);
                    trackBean.setType("tag");
                    iListResponse.responseTrackList(trackBean);
                }
            }

            @Override
            public void onResponse(String data, String finalUrl) throws IOException {
                try {

                    JSONObject jsonObject = new JSONObject(data);
                    trackBean.setTotal(jsonObject.optInt("total"));
                    trackBean.setOffset(jsonObject.optInt("offset"));
                    trackBean.setLimit(jsonObject.optInt("limit"));
                    trackBean.setSort(jsonObject.optString("sort"));
                    String items = jsonObject.optString("items");
                    Gson gson = new Gson();
                    List<Track> list = gson.fromJson(items, new TypeToken<List<Track>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            if (isCollected(list.get(i))) {
                                list.get(i).setCollected(true);
                                list.get(i).setFavor_id(favor_id);

                            } else {
                                list.get(i).setCollected(false);
                                list.get(i).setFavor_id(0);
                            }
                            String url = list.get(i).getPlay_url();
                            list.get(i).setPlay_url(AesUtil.encrypt(CommonConstants.KEY, url));
                            Log.e("zhangzimo", i + ">>>>" + list.get(i).getPlay_url());
                        }
                        trackBean.setSuccess(true);
                        trackBean.setType("tag");
                        trackBean.setTrackList(list);
                        iListResponse.responseTrackList(trackBean);
                    } else {
                        trackBean.setSuccess(false);
                        trackBean.setType("tag");
                        iListResponse.responseTrackList(trackBean);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    trackBean.setSuccess(false);
                    trackBean.setType("tag");
                    iListResponse.responseTrackList(trackBean);

                }

            }
        });
    }

    public void searchTagMusicByKey(String keyWord, final int pageIndex, final int pageSize) {
        if (!TextUtils.isEmpty(keyWord)) {
            HttpClientManager.searchTagMusicByKey(keyWord, pageIndex, pageSize, 1, 1, new ResultCallback<SearchTagSongNewResult>() {
                List<String> songIdList = new ArrayList<>();

                @Override
                public void onStart() {
                    Log.e(TAG, "searchTagMusicByKey onStart");

                }

                @Override
                public void onSuccess(SearchTagSongNewResult searchTagSongNewResult) {
                    Log.e(TAG, "searchTagMusicByKey onSuccess");
                    if (searchTagSongNewResult.getSearchTag().getData().getResult() != null && searchTagSongNewResult.getSearchTag().getData().getResult().length > 0) {
                        for (int i = 0; i < searchTagSongNewResult.getSearchTag().getData().getResult().length; i++) {
                            if (searchTagSongNewResult.getSearchTag().getData().getResult()[i].getFullSongs().length > 0) {
                                songIdList.add(searchTagSongNewResult.getSearchTag().getData().getResult()[i].getFullSongs()[0].getCopyrightId());
                            }
                        }
                    }
                    if (songIdList != null && songIdList.size() > 0) {
                        findMusicInfoByid(songIdList, "tag", pageIndex, pageSize);
                    }


                }

                @Override
                public void onFailed(String s, String s1) {
                    Log.e(TAG, "searchTagMusicByKey onFailed" + "s=" + s + "s1=" + s1);

                }

                @Override
                public void onFinish() {
                    Log.e(TAG, "searchTagMusicByKey onFinish");

                }
            });
        }

    }


    /**
     * 通过歌曲id查询歌曲信息
     *
     * @param songIdList
     */
    public void findMusicInfoByid(List<String> songIdList, String sort, int offset, int limit) {
        final int size = songIdList.size();
        final List<Track> trackList = new ArrayList<>();
        for (int i = 0; i < songIdList.size(); i++) {
            HttpClientManager.findMusicInfoByid(songIdList.get(i), new ResultCallback<MusicinfoResult>() {
                TrackBean trackBean = new TrackBean();

                @Override
                public void onStart() {
                    Log.e(TAG, "findMusicInfoByid onStart");

                }

                @Override
                public void onSuccess(MusicinfoResult musicinfoResult) {
                    Track track = new Track();
                    track.setPlay_url(musicinfoResult.getMusicInfo().getListenUrl());
                    //id 改成string
//                    track.setId(musicinfoResult.getMusicInfo().getMusicId());
                    track.setTitle(musicinfoResult.getMusicInfo().getMusicName());
                    track.setDuration(Integer.parseInt(musicinfoResult.getMusicInfo().getDuration()));
                    Album album = new Album();
//                    album.setId(musicinfoResult.getMusicInfo().getAlbumsID());
                    album.setTitle(musicinfoResult.getMusicInfo().getSingerName());
                    track.setAlbum(album);
                    Announcer announcer = new Announcer();
//                    announcer.setId(musicinfoResult.getMusicInfo().getSingerID());
                    announcer.setNickname(musicinfoResult.getMusicInfo().getSingerName());
                    track.setAnnouncer(announcer);
                    trackList.add(track);
                    if (isCollected(track)) {
                        track.setCollected(true);
                        track.setFavor_id(favor_id);

                    } else {
                        track.setCollected(false);
                        track.setFavor_id(0);
                    }

                    if (trackList.size() == size) {
//                        trackBean.setTotal();
//                        trackBean.setOffset();
                        trackBean.setLimit(size);
//                        trackBean.setSort();

                        trackBean.setSuccess(true);
                        trackBean.setType("tag");
                        trackBean.setTrackList(trackList);
                        iListResponse.responseTrackList(trackBean);
                    }
                }

                @Override
                public void onFailed(String s, String s1) {
                    Log.e(TAG, "findMusicInfoByid onFailed" + "s=" + s + "s1=" + s1);
                    trackBean.setSuccess(false);
                    trackBean.setType("tag");
                    iListResponse.responseTrackList(trackBean);


                }

                @Override
                public void onFinish() {
                    Log.e(TAG, "findMusicInfoByid onFinish");

                }
            });
        }

    }


    /**
     * 获取专辑下歌曲列表
     */
    public void requestTrackList(int albumID, int offset, final int limit) {
        String finalUrl = NetWorkUrLConstants.TRACKLIST_ALBUM_URL + "?albumID=" + albumID + "&sort=asc" + "&offset=" + offset + "&limit=" + limit;
        RequestNet.baseGetRequest(finalUrl, new IHttpCallback() {
            TrackBean trackBean = new TrackBean();

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                if (errorCode == 512) {
                    refresh();
                } else {
                    trackBean.setSuccess(false);
                    trackBean.setType("album");
                    iListResponse.responseTrackList(trackBean);
                }
            }

            @Override
            public void onResponse(String data, String finalUrl) throws IOException {
                try {

                    JSONObject jsonObject = new JSONObject(data);
                    trackBean.setTotal(jsonObject.optInt("total"));
                    trackBean.setOffset(jsonObject.optInt("offset"));
                    trackBean.setLimit(jsonObject.optInt("limit"));
                    trackBean.setSort(jsonObject.optString("sort"));
                    String items = jsonObject.optString("items");
                    Gson gson = new Gson();
                    List<Track> list = gson.fromJson(items, new TypeToken<List<Track>>() {
                    }.getType());

                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            if (isCollected(list.get(i))) {
                                list.get(i).setCollected(true);
                                list.get(i).setFavor_id(favor_id);

                            } else {
                                list.get(i).setCollected(false);
                                list.get(i).setFavor_id(0);
                            }
                            String url = list.get(i).getPlay_url();
                            list.get(i).setPlay_url(AesUtil.encrypt(CommonConstants.KEY, url));
                            Log.e("zhangzimo", i + ">>>>" + list.get(i).getPlay_url());

                        }
                        trackBean.setSuccess(true);
                        trackBean.setType("album");
                        trackBean.setTrackList(list);
                        iListResponse.responseTrackList(trackBean);
                    } else {
                        trackBean.setSuccess(false);
                        trackBean.setType("album");
                        iListResponse.responseTrackList(trackBean);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    trackBean.setSuccess(false);
                    trackBean.setType("album");
                    iListResponse.responseTrackList(trackBean);
                }

            }
        });
    }

    /**
     * 曲目搜索
     */
    public void requestSearchTrackList(String keywords, int offset, final int limit) {
        String finalUrl = NetWorkUrLConstants.SEARCH_TRACK_URL + "?keywords=" + keywords + "&categoryID=2" + "&sort=hottest" + "&offset=" + offset + "&limit=" + limit;
        RequestNet.baseGetRequest(finalUrl, new IHttpCallback() {
            TrackBean trackBean = new TrackBean();

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                if (errorCode == 512) {
                    refresh();
                } else {
                    trackBean.setSuccess(false);
                    trackBean.setType("search");
                    iListResponse.responseTrackList(trackBean);
                }
            }

            @Override
            public void onResponse(String data, String finalUrl) throws IOException {
                try {

                    JSONObject jsonObject = new JSONObject(data);
                    trackBean.setTotal(jsonObject.optInt("total"));
                    trackBean.setOffset(jsonObject.optInt("offset"));
                    trackBean.setLimit(jsonObject.optInt("limit"));
                    trackBean.setSort(jsonObject.optString("sort"));
                    String items = jsonObject.optString("items");
                    Gson gson = new Gson();
                    List<Track> list = gson.fromJson(items, new TypeToken<List<Track>>() {
                    }.getType());
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            if (isCollected(list.get(i))) {
                                list.get(i).setCollected(true);
                                list.get(i).setFavor_id(favor_id);

                            } else {
                                list.get(i).setCollected(false);
                                list.get(i).setFavor_id(0);
                            }
                            String url = list.get(i).getPlay_url();
                            list.get(i).setPlay_url(AesUtil.encrypt(CommonConstants.KEY, url));
                            Log.e("zhangzimo", i + ">>>>" + list.get(i).getPlay_url());
                        }
                        trackBean.setSuccess(true);
                        trackBean.setType("search");
                        trackBean.setTrackList(list);
                        iListResponse.responseTrackList(trackBean);

                    } else {
                        trackBean.setSuccess(false);
                        trackBean.setType("search");
                        iListResponse.responseTrackList(trackBean);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    trackBean.setSuccess(false);
                    trackBean.setType("search");
                    iListResponse.responseTrackList(trackBean);
                }

            }
        });

    }

    public void searchMusicByKey(String keyWord, final int offset, final int limit) {
        HttpClientManager.searchMusicByKey(keyWord, offset, limit, 1, 1, 1, new ResultCallback<SearchSongNewResult>() {
            @Override
            public void onStart() {
                Log.e(TAG, "searchMusicByKey onStart");

            }

            @Override
            public void onSuccess(SearchSongNewResult searchSongNewResult) {
                Log.e(TAG, "searchMusicByKey onSuccess");
                List<String> searchSongIdList = new ArrayList<>();
                if (searchSongNewResult.getSearchSong().getData().getResult() != null && searchSongNewResult.getSearchSong().getData().getResult().length > 0) {
                    for (int i = 0; i < searchSongNewResult.getSearchSong().getData().getResult().length; i++) {
                        if (searchSongNewResult.getSearchSong().getData().getResult()[i].getFullSongs().length > 0) {
                            searchSongIdList.add(searchSongNewResult.getSearchSong().getData().getResult()[i].getFullSongs()[0].getCopyrightId());
//                            Log.e(TAG, "length" + i + ">>>" + searchSongNewResult.getSearchSong().getData().getResult()[i].getFullSongs().length);
                        }

                    }
                }
                if (searchSongIdList != null && searchSongIdList.size() > 0) {
                    findMusicInfoByid(searchSongIdList, "search", offset, limit);
                }
            }

            @Override
            public void onFailed(String s, String s1) {
                Log.e(TAG, "searchMusicByKey onFailed" + "s=" + s + "s1=" + s1);

            }

            @Override
            public void onFinish() {
                Log.e(TAG, "searchMusicByKey onFinish");
            }
        });
    }

    /**
     * 添加收藏
     */
    public void addFavor(Track track) {
        Gson gson = new Gson();
        String content = gson.toJson(track);
        String finalUrl = NetWorkUrLConstants.ADD_FAVOR_URL;
        RequestNet.basePostRequest(finalUrl, 0, content, new IHttpCallback() {
            FavorResult favorResult = new FavorResult();

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                if (errorCode == 512) {
                    refresh();
                } else {
                    favorResult.setSuccess(false);
                    favorResult.setType("add");
                    iListResponse.responseFavorResult(favorResult);
                }

            }

            @Override
            public void onResponse(String data, String finalUrl) throws IOException {

                try {
                    JSONObject jsonObject = new JSONObject(data);
                    Gson gson = new Gson();
                    Favor favor = new Favor();
                    favor.setFavor_id(jsonObject.optInt("id"));
                    String content = jsonObject.optString("content");
                    Track track = gson.fromJson(content, Track.class);
                    favor.setTrack(track);
                    favorResult.setSuccess(true);
                    favorResult.setType("add");
                    favorResult.setFavor(favor);
                    iListResponse.responseFavorResult(favorResult);
                    requestFavorList();
                } catch (JSONException e) {
                    e.printStackTrace();
                    favorResult.setSuccess(false);
                    favorResult.setType("add");
                    iListResponse.responseFavorResult(favorResult);
                }


            }
        });

    }

    /**
     * 删除收藏
     */
    public void deleteFavor(int favorID) {
        String finalUrl = NetWorkUrLConstants.DELETE_FAVOR_URL;
        RequestNet.basePostRequest(finalUrl, 1, "" + favorID, new IHttpCallback() {
            FavorResult favorResult = new FavorResult();

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                if (errorCode == 512) {
                    refresh();
                } else {
                    favorResult.setSuccess(false);
                    favorResult.setType("delete");
                    iListResponse.responseFavorResult(favorResult);
                }

            }

            @Override
            public void onResponse(String data, String finalUrl) throws IOException {
                favorResult.setSuccess(true);
                favorResult.setType("delete");
                iListResponse.responseFavorResult(favorResult);
                requestFavorList();
            }
        });

    }

    /**
     * 获取收藏列表
     */
    public void requestFavorList() {
        RequestNet.baseGetRequest(NetWorkUrLConstants.FAVORLIST_URL + "?userID=" + CommonConstants.USER_ID, new IHttpCallback() {
            FavorBean favorBean = new FavorBean();

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                if (errorCode == 512) {
                    refresh();
                } else {
                    favorBean.setSuccess(false);
                    iListResponse.responseFavorList(favorBean);
                }

            }

            @Override
            public void onResponse(String data, String finalUrl) throws IOException {

                if (TextUtils.isEmpty(data)) {
                    favorBean.setSuccess(false);
                    iListResponse.responseFavorList(favorBean);

                } else {
                    try {
                        JSONArray jsonArray = new JSONArray(data);
                        List<Favor> favorList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.optJSONObject(i);
                            Favor favor = new Favor();
                            favor.setFavor_id(jsonObject.optInt("id"));
                            String content = jsonObject.optString("content");
                            Gson gson = new Gson();
                            favor.setTrack(gson.fromJson(content, Track.class));
                            favorList.add(favor);
                        }
                        favorBean.setSuccess(true);
                        favorBean.setFavorList(favorList);
                        iListResponse.responseFavorList(favorBean);
                        mFavorList = favorList;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        favorBean.setSuccess(false);
                        iListResponse.responseFavorList(favorBean);
                    }
                }
            }
        });

    }

    private boolean isCollected(Track track) {

        if (mFavorList != null && mFavorList.size() > 0) {
            for (int j = 0; j < mFavorList.size(); j++) {
                if (mFavorList.get(j).getTrack().getId() == track.getId()) {
                    favor_id = mFavorList.get(j).getFavor_id();
                    return true;
                }
            }
        }
        return false;
    }

    private CloudServiceImpl cloudService;
    CloudServiceImpl.IPC ipc = new CloudServiceImpl.IPC() {
        @Override
        public void connected() {
            cloudService.request(GsonUtil.getGsonInstance().toJson(new Req<>(app, "vin", null)));
            cloudService.request(GsonUtil.getGsonInstance().toJson(new Req<>(app, "loc", null)));
        }

        @Override
        public void disconnected() {

        }

        @Override
        public void response(String data) {
//            mHandler.obtainMessage(3, data).sendToTarget();
            LogUtils.e("response: " + data);
            handleJson(data);
        }
    };

    private void handleJson(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            String requestID = jsonObject.optString("requestID");
            LogUtils.e("requestID:" + requestID);
            if (TextUtils.isEmpty(requestID)) return;
            if ("vin".equals(requestID)) {
                Req<Vin> vin = GsonUtil.fromJson(data, Vin.class);
                CommonConstants.VIN = vin.getData().getVin();
                CommonConstants.TOKEN = vin.getData().getToken();
                CommonConstants.USER_ID = vin.getData().getUserID();
                LogUtils.e("CommonConstants.VIN=" + CommonConstants.VIN);
                LogUtils.e("CommonConstants.TOKEN=" + CommonConstants.TOKEN);
                LogUtils.e("CommonConstants.USER_ID=" + CommonConstants.USER_ID);
            } else if ("loc".equals(requestID)) {
                Req<Loc> loc = GsonUtil.fromJson(data, Loc.class);
                CommonConstants.CITY = loc.getData().getCity();
                CommonConstants.PROVINCE = loc.getData().getProvince();
                LogUtils.e("CommonConstants.CITY=" + CommonConstants.CITY);
                LogUtils.e("CommonConstants.PROVINCE=" + CommonConstants.PROVINCE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
