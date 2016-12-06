package com.jd.bluedragon.distribution.queryTool.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.queryTool.dao.ReverseReceiveDao;
import com.jd.bluedragon.distribution.queryTool.domain.ReverseReceive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by xumei1 on 2016/8/30.
 */

@Service("queryReverseReceiveService")
public class ReverseReceiveServiceImpl implements ReverseReceiveService {
    @Autowired
    ReverseReceiveDao queryReverseReceiveDao;

    @Override
    public List<ReverseReceive> queryByCondition(Map<String, Object> params, Pager<List<ReverseReceive>> page) {
        int count = queryReverseReceiveDao.countByCondition(params);
        if (page == null) {
            page = new Pager<List<ReverseReceive>>();
        }
        List<ReverseReceive> list = null;
        if (count > 0) {
            page.setTotalSize(count);
            page.init();

            params.put("startIndex",page.getStartIndex());
            params.put("endIndex",page.getEndIndex());
            params.put("pageSize",page.getPageSize());
            list = queryReverseReceiveDao.queryByCondition(params);
        }
        return list;
    }
}
