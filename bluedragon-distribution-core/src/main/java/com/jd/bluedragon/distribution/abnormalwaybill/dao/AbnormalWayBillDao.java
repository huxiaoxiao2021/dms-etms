package com.jd.bluedragon.distribution.abnormalwaybill.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运单异常操作dao
 * Created by shipeilin on 2017/11/17.
 */
public class AbnormalWayBillDao extends BaseDao<AbnormalWayBill> {
    public static final String namespace = AbnormalWayBillDao.class.getName();

    /**
     * 新增异常操作记录
     * @param abnormalOrder
     * @return
     */
    public int insert(AbnormalWayBill abnormalOrder){
        return super.getSqlSession().insert(namespace + ".add" , abnormalOrder);
    }

    /**
     * 批量新增异常操作记录
     * @param wayBillList
     * @return
     */
    public int addBatch(List<AbnormalWayBill> wayBillList) {
        return this.getSqlSession().insert(namespace + ".addBatch", wayBillList);
    }

    /**
     * 查询异常操作记录
     * @param wayBillCode
     * @param siteCode
     * @return
     */
    public AbnormalWayBill query(String wayBillCode, Integer siteCode){
        Map<String, Object> parameter = new HashMap<String, Object>(2);
        parameter.put("wayBillCode", wayBillCode);
        parameter.put("siteCode", siteCode);
        return super.getSqlSession().selectOne(namespace + ".get" , parameter);
    }
}
