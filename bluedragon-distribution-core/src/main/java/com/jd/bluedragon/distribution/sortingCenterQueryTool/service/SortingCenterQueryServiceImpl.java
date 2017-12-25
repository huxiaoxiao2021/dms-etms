package com.jd.bluedragon.distribution.sortingCenterQueryTool.service;

import com.alibaba.fastjson.TypeReference;
import com.jd.bluedragon.distribution.api.request.SortingCenterQueryRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.utils.RestHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by wuzuxiang on 2016/10/17.
 */
@Service("sortingCenterQueryService")
public class SortingCenterQueryServiceImpl implements SortingCenterQueryService{


    @Override
    public InvokeResult countNumFromThreeTables(SortingCenterQueryRequest request,String url) {

        return RestHelper.jsonPostForEntity(url,request,new TypeReference<InvokeResult<Long>>(){});
    }

    @Override
    public List<Object> queryDetailsFromThreeTables(Map<String,Object> request, String url) {
        return RestHelper.jsonPostForEntity(url,request,new TypeReference<List<Object>>(){});
    }
}
