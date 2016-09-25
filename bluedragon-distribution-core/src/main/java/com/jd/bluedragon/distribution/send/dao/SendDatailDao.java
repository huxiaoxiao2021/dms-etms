package com.jd.bluedragon.distribution.send.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;

public class SendDatailDao extends BaseDao<SendDetail> {
	
	public static final String namespace = SendDatailDao.class.getName();
	
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
		return this.getSqlSession().selectList(SendDatailDao.namespace + ".findSendDetails", param);
	}
	
	@SuppressWarnings("unchecked")
	public List<SendDetail> queryBySiteCodeAndSendCode(SendDetail query) {
		return this.getSqlSession().selectList(
				SendDatailDao.namespace + ".queryBySiteCodeAndSendCode", query);
	}
	
	@SuppressWarnings("unchecked")
	public List<SendDetail> querySendDatailsBySelective(SendDetail querySendDatail) {
        if(null != querySendDatail && StringHelper.isEmpty(querySendDatail.getSendCode())
                && null == querySendDatail.getCreateSiteCode()) {
            querySendDatail.setCreateSiteCode(SerialRuleUtil.getCreateSiteCodeFromSendCode(querySendDatail.getSendCode()));
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
		if (null != sendDetail && StringHelper.isEmpty(sendDetail.getSendCode())
				&& sendDetail.getCreateSiteCode() == null) {
			Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(sendDetail.getSendCode());
			sendDetail.setCreateSiteCode(createSiteCode);
		}
		return this.getSqlSession().update(SendDatailDao.namespace + ".cancelDelivery",
				sendDetail);
	}
	
	@SuppressWarnings("unchecked")
	public List<SendDetail> queryBySendCodeAndSendType(SendDetail query) {
		return this.getSqlSession().selectList(SendDatailDao.namespace + ".queryBySendCodeAndSendType",
				query);
	}
	
	@SuppressWarnings("unchecked")
	public List<SendDetail> batchQuerySendDList(SendDetail querySendDatail) {
		return this.getSqlSession().selectList(
				SendDatailDao.namespace + ".batchQuerySendDList", querySendDatail);
	}

	public List<SendDetail> queryWaybillsByPackCode(String packCode){
		return this.getSqlSession().selectList(
				SendDatailDao.namespace + ".queryWaybillsByPackCode", packCode);
	}

    public List<SendDetail> queryWaybillsByBoxCode(String boxCode){
        return this.getSqlSession().selectList(
                SendDatailDao.namespace + ".queryWaybillsByBoxCode", boxCode);
    }

    public List<SendDetail> queryWaybillsBySendCode(String sendCode){
        return this.getSqlSession().selectList(
                SendDatailDao.namespace + ".queryWaybillsBySendCode", sendCode);
    }

    public List<SendDetail> queryWaybillsByDepartID(Long departureID){
        return this.getSqlSession().selectList(
                SendDatailDao.namespace + ".queryWaybillsByDepartID", departureID);
    }


	public List<String> querySendCodesByDepartID(Long departureID){
		return this.getSqlSession().selectList(
				SendDatailDao.namespace + ".querySendCodesByDepartID", departureID);
	}
}