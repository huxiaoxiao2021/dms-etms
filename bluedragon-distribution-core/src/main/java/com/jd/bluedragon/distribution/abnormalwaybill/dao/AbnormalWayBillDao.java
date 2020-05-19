package com.jd.bluedragon.distribution.abnormalwaybill.dao;

import com.google.common.collect.Maps;
import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;

import java.util.ArrayList;
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
    public List<AbnormalWayBill> queryByWaveIds(List<String> waveIds){
        return super.getSqlSession().selectList(namespace + ".getByWaveIds" , waveIds);
    }
    /**
     * 通过班次id和 多运单，查异常处理情况
     * @param waveBusinessId
     * @param waybillCodeList
     * @return
     */
    public List<AbnormalWayBill> queryByWaveIdAndWaybillCodes(String waveBusinessId, ArrayList<String> waybillCodeList) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("waveBusinessId", waveBusinessId);
        param.put("waybillCodes", waybillCodeList);
        return super.getSqlSession().selectList(namespace + ".queryByWaveIdAndWaybillCodes", param);
    }

    /**
     * 查询异常操作记录
     * @param qcValue
     * @param createSiteCode
     * @return
     */
    public AbnormalWayBill getAbnormalWayBillByQcValue(Integer createSiteCode, String qcValue){
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("qcValue", qcValue);
        parameter.put("createSiteCode", createSiteCode);
        return super.getSqlSession().selectOne(namespace + ".getAbnormalWayBillByQcValue" , parameter);
    }
    /**
     * 根据运单号查询提报的异常,返回最后一次提交的异常记录
     * @param waybillCode
     * @return
     */
	public AbnormalWayBill queryAbnormalWayBillByWayBillCode(String waybillCode) {
        Map<String, Object> parameter = new HashMap<String, Object>(2);
        parameter.put("waybillCode", waybillCode);
        return super.getSqlSession().selectOne(namespace + ".queryAbnormalWayBillByWayBillCode" , parameter);
	}
}
