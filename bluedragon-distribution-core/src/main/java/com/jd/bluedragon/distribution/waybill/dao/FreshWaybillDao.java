package com.jd.bluedragon.distribution.waybill.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.waybill.domain.FreshWaybill;

import java.util.List;
import java.util.Map;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/4/28
 */
public class FreshWaybillDao extends BaseDao<FreshWaybill>{
    private static final String namespace = FreshWaybillDao.class.getName();

    public Integer addFreshWaybill(FreshWaybill waybill){
        return this.getSqlSession().insert(namespace + ".addFreshWaybill", waybill);
    }

    public List<FreshWaybill> getFreshWaybillByCode(FreshWaybill waybill){
        return this.getSqlSession().selectList(namespace + ".getFreshWaybillByCode", waybill);
    }

    public List<FreshWaybill> getFreshWaybillByID(FreshWaybill freshWaybill) {
        return this.getSqlSession().selectList(namespace + ".getFreshWaybillByID", freshWaybill);
    }

    public List<FreshWaybill> getFreshWaybillPage(Map<String, Object> param){
        return this.getSqlSession().selectList(namespace + ".getFreshWaybillPage", param);
    }

    public Integer getFreshWaybillCountByCode(FreshWaybill waybill){
        return (Integer) this.getSqlSession().selectOne(namespace + ".getFreshWaybillCountByCode", waybill);
    }

    public Integer delFreshWaybillByID(FreshWaybill waybill) {
        return this.getSqlSession().update(namespace + ".delFreshWaybillByID", waybill);
    }

}
