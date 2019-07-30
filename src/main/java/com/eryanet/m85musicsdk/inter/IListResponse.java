package com.eryanet.m85musicsdk.inter;

import com.eryanet.m85musicsdk.bean.AlbumBean;
import com.eryanet.m85musicsdk.bean.CategoryBean;
import com.eryanet.m85musicsdk.bean.FavorBean;
import com.eryanet.m85musicsdk.bean.FavorResult;
import com.eryanet.m85musicsdk.bean.RecommendBean;
import com.eryanet.m85musicsdk.bean.RequestResult;
import com.eryanet.m85musicsdk.bean.Track;
import com.eryanet.m85musicsdk.bean.TrackBean;

import java.util.List;

/**
 * Created by zhangjiao on 2018/7/25.
 */

public interface IListResponse {
    /**
     * 推荐专辑列表回调
     */
    public void responseRecommendList(RecommendBean recommendBean);

    /**
     * 分类/标签列表回调
     */
    public void responseCategoryList(CategoryBean categoryBean);


    /**
     * 歌单列表回调
     */
    public void responseTrackList(TrackBean trackBean);

    /**
     * 收藏列表回调
     */
    public void responseFavorList(FavorBean favorBean);
    /**
     * 收藏结果回调
     */
    public void responseFavorResult(FavorResult favorResult);

//    public void responseList(RequestResult<List<Track>>  requestResult);
}
