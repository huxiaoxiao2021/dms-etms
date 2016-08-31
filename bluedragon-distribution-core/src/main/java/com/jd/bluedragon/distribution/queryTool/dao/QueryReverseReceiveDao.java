package com.jd.bluedragon.distribution.queryTool.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.queryTool.domain.QueryReverseReceiveDomain;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by xumei1 on 2016/8/30.
 */
public class QueryReverseReceiveDao extends BaseDao<QueryReverseReceiveDomain> {
    private final Log logger = LogFactory.getLog(this.getClass());

    private static final String namespace = QueryReverseReceiveDao.class.getName();

    public List<QueryReverseReceiveDomain> queryByCondition(Map<String, Object> params) {
        logger.info("QueryReverseReceiveDao.queryByCondition begining...");
        return super.getSqlSession().selectList(QueryReverseReceiveDao.namespace + ".queryByCondition",params);
    }

    public Integer countByCondition(Map<String,Object> params){
        logger.info("QueryReverseReceiveDao.countByCondition begining...");
        return super.getSqlSession().selectOne(QueryReverseReceiveDao.namespace+".countByCondition",params);
    }
}
