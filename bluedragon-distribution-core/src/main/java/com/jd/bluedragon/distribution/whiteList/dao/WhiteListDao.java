package com.jd.bluedragon.distribution.whiteList.dao;

import com.jd.bluedragon.common.dao.BaseDao;

import com.jd.bluedragon.distribution.whitelist.WhiteList;
import com.jd.bluedragon.distribution.whitelist.WhiteListCondition;

import java.util.List;

/**
 * @author lijie
 * @date 2020/3/10 16:59
 */
public class WhiteListDao extends BaseDao<WhiteList> {

    public static final String namespace = WhiteListDao.class.getName();

    public List<WhiteList> queryByCondition(WhiteListCondition condition){
        return this.getSqlSession().selectList(namespace+".queryByCondition", condition);
    }

    public Integer query(WhiteList whiteList){
        return this.getSqlSession().selectOne(namespace+".query", whiteList);
    }

    public Integer save(WhiteList whiteList) {
        return this.getSqlSession().insert(namespace+".save", whiteList);
    }

    public Integer deleteByIds(List<Long> ids) {
        return this.getSqlSession().delete(namespace+".deleteByIds", ids);
    }
}
