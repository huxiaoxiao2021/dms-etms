package com.jd.bluedragon.distribution.cacheClean.service;

import com.alibaba.fastjson.TypeReference;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.CacheCleanRequest;
import com.jd.bluedragon.distribution.api.response.CacheCleanResponse;
import com.jd.bluedragon.distribution.cacheClean.domain.CacheClean;
import com.jd.bluedragon.utils.RestHelper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zhoutao on 2017/6/13.
 */
@Service("cacheCleanService")
public class CacheCleanServiceImpl implements CacheCleanService {

    @Override
    public CacheCleanResponse<Pager<List<CacheClean>>>  findPageCacheClean(CacheCleanRequest request, String url) {
        return RestHelper.jsonPostForEntity(url, request, //
                new TypeReference<CacheCleanResponse<Pager<List<CacheClean>>>>() {
                });
    }

    public CacheCleanResponse<Integer> cacheClean(CacheCleanRequest request,String url){
        return RestHelper.jsonPostForEntity(url,request, new TypeReference<CacheCleanResponse<Integer>>(){

        });

    }
}






























