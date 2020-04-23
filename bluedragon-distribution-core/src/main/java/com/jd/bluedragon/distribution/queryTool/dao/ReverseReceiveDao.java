package com.jd.bluedragon.distribution.queryTool.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.queryTool.domain.ReverseReceive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by xumei1 on 2016/8/30.
 */
public class ReverseReceiveDao extends BaseDao<ReverseReceive> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String namespace = ReverseReceiveDao.class.getName();

    public List<ReverseReceive> queryByCondition(Map<String, Object> params) {
        log.info("ReverseReceiveDao.queryByCondition begining...");
        return super.getSqlSession().selectList(ReverseReceiveDao.namespace + ".queryByCondition",params);
    }

    public Integer countByCondition(Map<String,Object> params){
        log.info("ReverseReceiveDao.countByCondition begining...");
        return super.getSqlSession().selectOne(ReverseReceiveDao.namespace+".countByCondition",params);
    }
}
