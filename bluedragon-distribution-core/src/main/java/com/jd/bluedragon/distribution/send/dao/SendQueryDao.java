package com.jd.bluedragon.distribution.send.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.send.domain.SendQuery;

import java.util.List;

/**
 * Created by wangtingwei on 2014/12/5.
 */
public class SendQueryDao extends BaseDao<SendQuery> {
    private static final String namespace=SendQueryDao.class.getName();
    public List<SendQuery> queryBySendCode(String sendCode){
        return this.getSqlSession().selectList(namespace+".queryBySendCode",sendCode);
    }

    public boolean add(SendQuery domain){
        return this.add(namespace,domain)>0;
    }
}
