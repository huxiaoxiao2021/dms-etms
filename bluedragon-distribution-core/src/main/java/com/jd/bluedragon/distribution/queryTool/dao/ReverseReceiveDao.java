package com.jd.bluedragon.distribution.queryTool.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.queryTool.domain.ReverseReceive;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by xumei1 on 2016/8/30.
 */
public class ReverseReceiveDao extends BaseDao<ReverseReceive> {
    private final Log logger = LogFactory.getLog(this.getClass());

    private static final String namespace = ReverseReceiveDao.class.getName();

    public List<ReverseReceive> queryByCondition(Map<String, Object> params) {
        logger.info("ReverseReceiveDao.queryByCondition begining...");
        return super.getSqlSession().selectList(ReverseReceiveDao.namespace + ".queryByCondition",params);
    }

    public Integer countByCondition(Map<String,Object> params){
        logger.info("ReverseReceiveDao.countByCondition begining...");
        return super.getSqlSession().selectOne(ReverseReceiveDao.namespace+".countByCondition",params);
    }
}
