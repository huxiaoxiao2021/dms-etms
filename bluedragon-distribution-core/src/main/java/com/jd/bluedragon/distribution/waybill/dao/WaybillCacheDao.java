package com.jd.bluedragon.distribution.waybill.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.common.router.CacheTablePartition;

import java.util.HashMap;
import java.util.Map;

public class WaybillCacheDao extends BaseDao<WaybillCache> {

    public static final String namespace = WaybillCacheDao.class.getName();

    public WaybillCache findByWaybillCode(String waybillCode) {
        return super.getSqlSession().selectOne(namespace + ".findByWaybillCode", generateParamMap(waybillCode));
    }

    public String getRouterByWaybillCode(String waybillCode) {
        return super.getSqlSession().selectOne(namespace+".getRouterByWaybillCode", generateParamMap(waybillCode));
    }

    private Map generateParamMap(String waybillCode) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("waybillCode", waybillCode);

        //查询前重新计算分配的db和tableName
        String db = CacheTablePartition.getDmsCacheDb(waybillCode);
        String tableName = CacheTablePartition.getDmsWaybillCacheTableName(waybillCode);
        paramMap.put("db", db);
        paramMap.put("tableName", tableName);
        return paramMap;
    }
}
