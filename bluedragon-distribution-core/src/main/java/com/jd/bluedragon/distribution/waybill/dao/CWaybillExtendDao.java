package com.jd.bluedragon.distribution.waybill.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.ver.domain.CWaybillExtend;
import com.jd.bluedragon.distribution.waybill.router.CacheTablePartition;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xumei3 on 2017/8/14.
 */
public class CWaybillExtendDao extends BaseDao<CWaybillExtend> {

    public static final String namespace = CWaybillExtendDao.class.getName();

    public boolean insertOnDuplicate(CWaybillExtend waybill) {
        return super.getSqlSession().insert(CWaybillExtendDao.namespace + ".insertOnDuplicate", waybill) > 0 ? true : false;
    }

    public int batchInsertOnDuplicate(String tableName, List<CWaybillExtend> waybillExtends) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("tableName", tableName);
        parameters.put("list", waybillExtends);
        return super.getSqlSession().insert(CWaybillExtendDao.namespace + ".batchInsertOnDuplicate", parameters);
    }

    public CWaybillExtend get(CWaybillExtend waybill) {
        return (CWaybillExtend) super.getSqlSession().selectOne(CWaybillExtendDao.namespace + ".get", waybill);
    }

    public CWaybillExtend getWeightByWaybillCode(String waybillCode){
       return (CWaybillExtend) super.getSqlSession().selectOne(CWaybillExtendDao.namespace + ".getWeightByWaybillCode", generateParamMap(waybillCode));
    }

    @JProfiler(jKey = "DMSVER.CWaybillExtendDao.findCWaybillExtendByWaybillCode", mState = {JProEnum.TP})
    public CWaybillExtend findCWaybillExtendByWaybillCode(String waybillCode) {
        return super.getSqlSession().selectOne(namespace + ".findCWaybillExtendByWaybillCode", generateParamMap(waybillCode));
    }
    @JProfiler(jKey = "DMSVER.CWaybillExtendDao.findByWaybillCode", mState = {JProEnum.TP})
    public Waybill findByWaybillCode(String waybillCode) {
        return super.getSqlSession().selectOne(namespace + ".findByWaybillCode", generateParamMap(waybillCode));
    }
    @JProfiler(jKey = "DMSVER.CWaybillExtendDao.getLocalWaybillByWaybillCode", mState = {JProEnum.TP})
    public Waybill getLocalWaybillByWaybillCode(String waybillCode) {
        return super.getSqlSession().selectOne(namespace + ".getLocalWaybillByWaybillCode", generateParamMap(waybillCode));
    }

    public List<Waybill> findByTableName(Map<String, Object> paramMap){
        return super.getSqlSession().selectList(namespace + ".findByTableName", paramMap);
    }

    public String getRouterByWaybillCode(String waybillCode){
        return super.getSqlSession().selectOne(namespace+".getRouterByWaybillCode",generateParamMap(waybillCode));
    }

    public Integer updateQuantity(String waybillCode, Integer quantity){
        Map<String, Object> parameters = generateParamMap(waybillCode);
        parameters.put("quantity", quantity);
        return super.getSqlSession().update(namespace + ".updateQuantity", parameters);

    }

    private Map generateParamMap(String waybillCode){
        Map paramMap = new HashMap();
        String tableName = null;
        paramMap.put("waybillCode",waybillCode);

        //查询前重新计算分配的db和tableName
        String db = CacheTablePartition.getDmsCacheDb(waybillCode);
        tableName = CacheTablePartition.getDmsWaybillCacheTableName(waybillCode);
        paramMap.put("db", db);
        paramMap.put("tableName", tableName);
        return paramMap;
    }
}
