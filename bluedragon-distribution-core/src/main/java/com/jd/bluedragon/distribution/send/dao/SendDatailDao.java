package com.jd.bluedragon.distribution.send.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class SendDatailDao extends BaseDao<SendDetail> {

    public static final String namespace = SendDatailDao.class.getName();
    private Integer createSiteCode;

    @SuppressWarnings("unchecked")
    public List<SendDetail> querySendDatailsByBoxCode(SendDetail tSendDatail) {
        return this.getSqlSession().selectList(
                SendDatailDao.namespace + ".querySendDatailsByBoxCode", tSendDatail);
    }

    @SuppressWarnings("unchecked")
    public List<SendDetail> querySendDatailsByPackageCode(SendDetail tSendDatail) {
        return this.getSqlSession().selectList(
                SendDatailDao.namespace + ".querySendDatailsByPackageCode", tSendDatail);
    }

    public SendDetail querySendDatailBySendStatus(SendDetail tSendDatail) {
        return (SendDetail) this.getSqlSession().selectOne(
                SendDatailDao.namespace + ".querySendDatailBySendStatus", tSendDatail);
    }

    public SendDetail queryOneSendDatailByBoxCode(String boxCode) {
        return (SendDetail) this.getSqlSession().selectOne(
                SendDatailDao.namespace + ".queryOneSendDatailByBoxCode", boxCode);
    }

    @SuppressWarnings("unchecked")
    public List<SendDetail> findSendDetails(SendDetail param) {
        if (null != param && StringHelper.isNotEmpty(param.getSendCode())
                && null == param.getCreateSiteCode()) {
            param.setCreateSiteCode(SerialRuleUtil.getCreateSiteCodeFromSendCode(param.getSendCode()));
        }
        return this.getSqlSession().selectList(SendDatailDao.namespace + ".findSendDetails", param);
    }

    @SuppressWarnings("unchecked")
    public List<SendDetail> queryBySiteCodeAndSendCode(SendDetail query) {
        return this.getSqlSession().selectList(
                SendDatailDao.namespace + ".queryBySiteCodeAndSendCode", query);
    }

    @SuppressWarnings("unchecked")
    public List<SendDetail> querySendDatailsBySelective(SendDetail querySendDatail) {
        if (null != querySendDatail && StringHelper.isNotEmpty(querySendDatail.getSendCode())
                && null == querySendDatail.getCreateSiteCode()) {
            Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(querySendDatail.getSendCode());
            querySendDatail.setCreateSiteCode(createSiteCode);
            if (null == createSiteCode) //为null说明不是合法的批次号,直接返回空List
                return new ArrayList<SendDetail>();
        }

        return this.getSqlSession().selectList(
                SendDatailDao.namespace + ".querySendDatailsBySelective", querySendDatail);
    }

    public boolean updateSendDatail(SendDetail SendDatail) {
        return this.getSqlSession().update(SendDatailDao.namespace + ".updateSendDatail",
                SendDatail) > 0;
    }

    public boolean cancelSendDatail(SendDetail sendDetial) {
        sendDetial.setCreateTime(sendDetial.getOperateTime());
        return this.getSqlSession().update(SendDatailDao.namespace + ".cancelSendDatail",
                sendDetial) > 0;
    }

    @SuppressWarnings("unchecked")
    public List<SendDetail> findUpdatewaybillCodeMessage() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("startTime", DateHelper.addDate(new Date(), -3));
        return this.getSqlSession().selectList(
                SendDatailDao.namespace + ".findUpdatewaybillCodeMessage", paramMap);
    }

    public boolean updatewaybillCodeStatus(SendDetail sendDetial) {
        sendDetial.setCreateTime(sendDetial.getOperateTime());
        return this.getSqlSession().update(SendDatailDao.namespace + ".UpdatewaybillCodeStatus",
                sendDetial) > 0;
    }

    public boolean updateSendStatusByPackage(SendDetail sendDetial) {
        sendDetial.setCreateTime(sendDetial.getOperateTime());
        return this.getSqlSession().update(SendDatailDao.namespace + ".updateSendStatusByPackage",
                sendDetial) > 0;
    }

    public boolean updateWeight(SendDetail sendDetail) {
        return this.getSqlSession().update(SendDatailDao.namespace + ".updateWeight", sendDetail) > 0;
    }

    public Boolean canCancel(SendDetail sendDetail) {
        Integer count = this.getSqlSession().update(SendDatailDao.namespace + ".canCancel",
                sendDetail);
        return count > 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    public Boolean canCancel2(SendDetail sendDetail) {
        Integer count = this.getSqlSession().update(SendDatailDao.namespace + ".canCancel2",
                sendDetail);
        return count > 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    public Boolean canCancelFuzzy(SendDetail sendDetail) {
        Integer count = this.getSqlSession().update(SendDatailDao.namespace + ".canCancelFuzzy",
                sendDetail);
        return count > 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    public Integer updateCancel(SendDetail sendDetail) {
        return this.getSqlSession().update(SendDatailDao.namespace + ".updateCancelStasus",
                sendDetail);
    }

    public Integer updateCancelBatch(SendDetail sendDetail) {
        return this.getSqlSession().update(SendDatailDao.namespace + ".updateCancelBatch",
                sendDetail);
    }

    @SuppressWarnings("unchecked")
    public List<SendDetail> querySendCodesByWaybills(String waybillCodeIn) {
        SendDetail querySendDatail = new SendDetail();
        querySendDatail.setWaybillCode(waybillCodeIn);
        return this.getSqlSession().selectList(
                SendDatailDao.namespace + ".querySendCodesByWaybills", querySendDatail);
    }

    @SuppressWarnings("unchecked")
    public List<SendDetail> querySendCodesByWaybill(String waybillCodeIn) {
        SendDetail querySendDatail = new SendDetail();
        querySendDatail.setWaybillCode(waybillCodeIn);
        return this.getSqlSession().selectList(
                SendDatailDao.namespace + ".querySendCodesByWaybill", querySendDatail);
    }

    @SuppressWarnings("unchecked")
    public List<SendDetail> getSendSiteID(SendDetail sendDetail) {
        return this.getSqlSession().selectList(SendDatailDao.namespace + ".getSendSiteID",
                sendDetail);
    }

    public boolean checkSendByPackage(SendDetail sendDetail) {
        return (Integer) this.getSqlSession().selectOne(
                SendDatailDao.namespace + ".checkSendByPackage", sendDetail) > 0;
    }

    public boolean checkSendSendCode(SendDetail sendDetail) {
        return (Integer) this.getSqlSession().selectOne(
                SendDatailDao.namespace + ".checkSendSendCode", sendDetail) > 0;
    }

    @SuppressWarnings("unchecked")
    public List<SendDetail> findOrder(SendDetail query) {
        return this.getSqlSession().selectList(SendDatailDao.namespace + ".findOrder", query);
    }

    @SuppressWarnings("unchecked")
    public List<SendDetail> findDeliveryPackageBySite(SendDetail query) {
        return this.getSqlSession().selectList(
                SendDatailDao.namespace + ".findDeliveryPackageBySite", query);
    }

    @SuppressWarnings("unchecked")
    public List<SendDetail> findDeliveryPackageByCode(SendDetail query) {
        return this.getSqlSession().selectList(
                SendDatailDao.namespace + ".findDeliveryPackageByCode", query);
    }

    @SuppressWarnings("unchecked")
    public List<SendDetail> querySortingDiff(SendDetail query) {
        return this.getSqlSession()
                .selectList(SendDatailDao.namespace + ".querySortingDiff", query);
    }

    @SuppressWarnings("unchecked")
    public List<SendDetail> queryWithoutPackageNum(SendDetail query) {
        return this.getSqlSession().selectList(SendDatailDao.namespace + ".queryWithoutPackageNum",
                query);
    }

    public Integer updatePackageNum(SendDetail sendDetail) {
        return this.getSqlSession().update(SendDatailDao.namespace + ".updatePackageNum",
                sendDetail);
    }

    public Integer cancelDelivery(SendDetail sendDetail) {
        if (null != sendDetail && StringHelper.isNotEmpty(sendDetail.getSendCode())
                && sendDetail.getCreateSiteCode() == null) {
            Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(sendDetail.getSendCode());
            sendDetail.setCreateSiteCode(createSiteCode);
            if (null == createSiteCode) //为null说明不是合法的批次号,直接返回空List
                return 0;
        }
        return this.getSqlSession().update(SendDatailDao.namespace + ".cancelDelivery",
                sendDetail);
    }

    @SuppressWarnings("unchecked")
    public List<SendDetail> queryBySendCodeAndSendType(SendDetail query) {
        if (null != query && StringHelper.isNotEmpty(query.getSendCode())
                && query.getCreateSiteCode() == null) {
            Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(query.getSendCode());
            query.setCreateSiteCode(createSiteCode);
            if (null == createSiteCode) //为null说明不是合法的批次号,直接返回空List
                return new ArrayList<SendDetail>();
        }
        return this.getSqlSession().selectList(SendDatailDao.namespace + ".queryBySendCodeAndSendType",
                query);
    }

    @SuppressWarnings("unchecked")
    public List<SendDetail> batchQuerySendDList(SendDetail querySendDatail) {
        return this.getSqlSession().selectList(
                SendDatailDao.namespace + ".batchQuerySendDList", querySendDatail);
    }

    public List<SendDetail> queryWaybillsByPackCode(String packCode) {
        return this.getSqlSession().selectList(
                SendDatailDao.namespace + ".queryWaybillsByPackCode", packCode);
    }

    public List<SendDetail> queryWaybillsByBoxCode(String boxCode) {
        SendDetail query = new SendDetail();
        query.setBoxCode(boxCode);
        return this.queryWaybillsByBoxCode(query);
    }

    protected List<SendDetail> queryWaybillsByBoxCode(SendDetail query) {
        return this.getSqlSession().selectList(
                SendDatailDao.namespace + ".queryWaybillsByBoxCode", query);
    }

    public List<SendDetail> queryWaybillsBySendCode(String sendCode) {
        SendDetail query = new SendDetail();
        query.setSendCode(sendCode);

        Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(query.getSendCode());
        query.setCreateSiteCode(createSiteCode);
        if (null == createSiteCode) //为null说明不是合法的批次号,直接返回空List
            return new ArrayList<SendDetail>();

        return this.getSqlSession().selectList(
                SendDatailDao.namespace + ".queryWaybillsBySendCode", query);
    }

    public List<SendDetail> querySendDetailBySendCode(String sendCode){
        SendDetail query = new SendDetail();
        query.setSendCode(sendCode);
        Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(query.getSendCode());
        query.setCreateSiteCode(createSiteCode);
        if (null == createSiteCode) //为null说明不是合法的批次号,直接返回空List
            return new ArrayList<SendDetail>();

        return this.getSqlSession().selectList(
                SendDatailDao.namespace + ".querySendDetailBySendCode", query);

    }


    //wzx 2016-12-29 15:12:19
    public List<SendDetail> queryBoxCodeBySendCode(String sendCode) {
        SendDetail query = new SendDetail();
        query.setSendCode(sendCode);

        Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(query.getSendCode());
        query.setCreateSiteCode(createSiteCode);
        if (null == createSiteCode) //为null说明不是合法的批次号,直接返回空List
            return new ArrayList<SendDetail>();

        return this.getSqlSession().selectList(
                SendDatailDao.namespace + ".queryBoxCodeBySendCode", query);
    }

    public List<SendDetail> queryWaybillsByDepartID(Long departureID) {
        return this.getSqlSession().selectList(
                SendDatailDao.namespace + ".queryWaybillsByDepartID", departureID);
    }


    public List<String> querySendCodesByDepartID(Long departureID) {
        return this.getSqlSession().selectList(
                SendDatailDao.namespace + ".querySendCodesByDepartID", departureID);
    }

    public Integer querySendDCountBySendCode(String sendCode) {
        Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCode);
        if (null == createSiteCode) { //为null说明不是合法的批次号,直接返回空List
            return 0;
        }
        SendDetail query = new SendDetail();
        query.setSendCode(sendCode);
        query.setCreateSiteCode(createSiteCode);
        return this.getSqlSession().selectOne(SendDatailDao.namespace + ".querySendDCountBySendCode", query);
    }

    /**
     * 根据运单号，查询所有包裹号
     * @param sendDetail 运单号
     * @return
     */
    public List<SendDetail>  findPackageBoxCodesByWaybillCode( SendDetail sendDetail){
        return this.getSqlSession().selectList(namespace + ".findPackageBoxCodesByWaybillCode", sendDetail);
    }
    /**
     * 查询SendD列表数据
     * @param sendDetail
     * @return
     */
    public List<SendDetail>  queryListByCondition(SendDetail sendDetail){
        return this.getSqlSession().selectList(namespace + ".queryListByCondition", sendDetail);
    }

    /**
     * 根据运单或包裹号查询已发货数据
     * @param sendDetail
     * @return
     */
    public List<SendDetail>  findByWaybillCodeOrPackageCode(SendDetail sendDetail){
        return this.getSqlSession().selectList(namespace + ".findByWaybillCodeOrPackageCode", sendDetail);
    }

    /**
     * 根据始发分拣中心，目的分拣中心，包裹号查询一条发货记录
     * @param sendDetail
     * @return
     */
    public SendDetail queryOneSendDetailByPackageCode(SendDetail sendDetail){
        if (sendDetail == null || sendDetail.getCreateSiteCode() == null ||
                sendDetail.getReceiveSiteCode() == null ||
                StringUtils.isBlank(sendDetail.getPackageBarcode())) {
            throw new IllegalArgumentException("始发分拣、目的分拣、包裹号不能为空.");
        }
        return this.getSqlSession().selectOne(namespace+".queryOneSendDetailByPackageCode",sendDetail);
    }
}
