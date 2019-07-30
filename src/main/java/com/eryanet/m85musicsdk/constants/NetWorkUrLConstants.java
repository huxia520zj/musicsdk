package com.eryanet.m85musicsdk.constants;

public class NetWorkUrLConstants {
    public static final String BASE_URL = "https://cloud.eryanet.com/entertainment";
//    public static final String BASE_URL = "http://192.168.8.28:9001";

    //VIN登陆
    public static final String VIN_LOGIN_URL = "https://cloud.eryanet.com/account/user/loginVin";


    //token 刷新
    public static final String TOKEN_BASE_URL = "https://cloud.eryanet.com/account";

    //获取推荐专辑列表
    public static final String RECOMMENDLIST_URL = BASE_URL+"/music/recommends";
    //获取分类列表
    public static final String CATEGORYLIST_URL = BASE_URL+"/music/tags";
    //获取标签下对应的歌曲列表
    public static final String TRACKLIST_TAG_URL = BASE_URL+"/music/tracks";
    //获取专辑下对应歌曲列表
    public static final String TRACKLIST_ALBUM_URL = BASE_URL+"/radio/albumDetail";
    //曲目搜索
    public static final String SEARCH_TRACK_URL = BASE_URL+"/radio/searchTracks";
    //添加收藏
    public static final String ADD_FAVOR_URL = BASE_URL+"/collection/music/add";
    //删除收藏
    public static final String DELETE_FAVOR_URL = BASE_URL+"/collection/music/delete";
    //收藏列表
    public static final String FAVORLIST_URL = BASE_URL+"/collection/music/list";





}
