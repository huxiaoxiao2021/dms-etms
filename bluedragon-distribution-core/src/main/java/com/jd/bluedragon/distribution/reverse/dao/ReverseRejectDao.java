package com.jd.bluedragon.distribution.reverse.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReject;

public class ReverseRejectDao extends BaseDao<ReverseReject> {
    
    public static final String namespace = ReverseRejectDao.class.getName();
    
    public ReverseReject get(ReverseReject reverseReject) {
        return (ReverseReject) super.getSqlSession().selectOne(ReverseRejectDao.namespace + ".get",
                reverseReject);
    }
    
}
