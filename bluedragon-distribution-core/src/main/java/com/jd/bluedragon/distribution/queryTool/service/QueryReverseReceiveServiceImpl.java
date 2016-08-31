package com.jd.bluedragon.distribution.queryTool.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.queryTool.dao.QueryReverseReceiveDao;
import com.jd.bluedragon.distribution.queryTool.domain.QueryReverseReceiveDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by xumei1 on 2016/8/30.
 */

@Service("queryReverseReceiveService")
public class QueryReverseReceiveServiceImpl implements QueryReverseReceiveService{
    @Autowired
    QueryReverseReceiveDao queryReverseReceiveDao;

    @Override
    public List<QueryReverseReceiveDomain> queryByCondition(Map<String, Object> params, Pager<List<QueryReverseReceiveDomain>> page) {
        int count = queryReverseReceiveDao.countByCondition(params);
        if (page == null) {
            page = new Pager<List<QueryReverseReceiveDomain>>();
        }
        List<QueryReverseReceiveDomain> list = null;
        if (count > 0) {
            page.setTotalSize(count);
            page.init();

            params.put("startIndex",page.getStartIndex());
            params.put("endIndex",page.getEndIndex());
            list = queryReverseReceiveDao.queryByCondition(params);
        }
        return list;
    }
}
