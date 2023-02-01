package com.jd.bluedragon.distribution.abnormalwaybill.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWaybillDiff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运单异常操作dao
 * Created by shipeilin on 2017/11/17.
 */
public class AbnormalWaybillDiffDao extends BaseDao<AbnormalWaybillDiff> {
    public static final String namespace = AbnormalWaybillDiffDao.class.getName();

    /**
     * 新增异常操作记录
     * @param abnormalOrder
     * @return
     */
    public int insert(AbnormalWaybillDiff abnormalOrder){
        return super.getSqlSession().insert(namespace + ".add" , abnormalOrder);
    }

    /**
     * 批量新增异常操作记录
     * @param wayBillList
     * @return
     */
    public int addBatch(List<AbnormalWaybillDiff> wayBillList) {
        return this.getSqlSession().insert(namespace + ".addBatch", wayBillList);
    }

    /**
     * 查询异常记录
     * @param abnormalWaybillDiff
     * @return
     */
    public List<AbnormalWaybillDiff> query(AbnormalWaybillDiff abnormalWaybillDiff){
        return super.getSqlSession().selectList(namespace + ".query" , abnormalWaybillDiff);
    }

}
