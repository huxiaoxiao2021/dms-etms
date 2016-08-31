package com.jd.bluedragon.distribution.queryTool.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.queryTool.domain.QueryReverseReceiveDomain;

import java.util.List;
import java.util.Map;

/**
 * Created by xumei1 on 2016/8/30.
 */
public interface QueryReverseReceiveService {
    List<QueryReverseReceiveDomain> queryByCondition(Map<String,Object> params, Pager<List<QueryReverseReceiveDomain>> page);
}
