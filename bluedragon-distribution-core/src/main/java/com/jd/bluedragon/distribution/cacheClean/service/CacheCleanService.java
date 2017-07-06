package com.jd.bluedragon.distribution.cacheClean.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.CacheCleanRequest;
import com.jd.bluedragon.distribution.api.response.CacheCleanResponse;
import com.jd.bluedragon.distribution.cacheClean.domain.CacheClean;

import java.util.List;


/**
 * Created by zhoutao on 2017/6/13.
 */

public interface CacheCleanService {

    CacheCleanResponse<Pager<List<CacheClean>>> findPageCacheClean(CacheCleanRequest cacheCleanRequest, String url) ;

    CacheCleanResponse<Integer> cacheClean(CacheCleanRequest cacheCleanRequest,String url);

}
