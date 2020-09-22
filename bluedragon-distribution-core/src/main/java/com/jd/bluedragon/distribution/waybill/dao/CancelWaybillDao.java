package com.jd.bluedragon.distribution.waybill.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.waybill.domain.CancelWaybill;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CancelWaybillDao
 * @Description
 * @Author wangyinhui3
 * @Date 2019/12/18
 **/
public class CancelWaybillDao extends BaseDao<CancelWaybill> {

    private static final String NAMESPACE = CancelWaybillDao.class.getName();

    public List<CancelWaybill> getByWaybillCode(String waybillCode) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("waybillCode", waybillCode);
        return super.getSqlSession().selectList(NAMESPACE + ".getByWaybillCode", paramMap);
    }

    public boolean add(CancelWaybill cancelWaybill) {
        cancelWaybill.setTs(System.currentTimeMillis());
        return super.getSqlSession().update(NAMESPACE + ".add", cancelWaybill) > 0 ? true : false;
    }
}
