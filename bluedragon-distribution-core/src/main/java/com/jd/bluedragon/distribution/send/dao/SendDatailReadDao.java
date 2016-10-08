package com.jd.bluedragon.distribution.send.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.api.response.SendBoxDetailResponse;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.utils.StringHelper;

public class SendDatailReadDao extends BaseDao<SendDetail> {
	
	public static final String namespace = SendDatailReadDao.class.getName();
	
	@SuppressWarnings("unchecked")
	public List<SendDetail> findUpdatewaybillCodeMessage(List<String> queryCondition) {
		
		if(queryCondition==null || queryCondition.isEmpty())
			return null;
		String queueId = StringHelper.join(queryCondition, ",", "(", ")",
				"'");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("queueId", queueId);
		
		return this.getSqlSessionRead().selectList(
				SendDatailReadDao.namespace + ".findUpdatewaybillCodeMessage", paramMap);
	}
	
	/**
	 * 
	 * @param boxCode
	 * @param createSiteCode 可为空,为空时全库扫描,影响性能
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> findWaybillByBoxCode(String boxCode, Integer createSiteCode) {
		if (boxCode == null || boxCode.isEmpty())
			return null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("boxCode", boxCode);
		if (null != createSiteCode && createSiteCode > 0)
			paramMap.put("createSiteCode", createSiteCode);
		return this.getSqlSessionRead().selectList(SendDatailReadDao.namespace + ".findWaybillByBoxCode", paramMap);
	}

	@SuppressWarnings("unchecked")
	public List<SendBoxDetailResponse> findSendBoxByWaybillCode(String waybillCode) {
		if(waybillCode == null || waybillCode.isEmpty()){
			return null;
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("waybillCode", waybillCode);	
		List<SendDetail> sendDetailList = this.getSqlSessionRead().selectList(
				SendDatailReadDao.namespace + ".findSendBoxByWaybillCode", paramMap);
		
		List<SendBoxDetailResponse> sendResponseList = new ArrayList<SendBoxDetailResponse>();
		if(sendDetailList != null && sendDetailList.size() > 0){
			for(SendDetail sendDetail : sendDetailList){
				SendBoxDetailResponse response = new SendBoxDetailResponse();
				response.setBoxCode(sendDetail.getBoxCode());
				response.setPackagebarcode(sendDetail.getPackageBarcode());
				response.setSendCode(sendDetail.getSendCode());
				response.setCreateSiteCode(sendDetail.getCreateSiteCode());
				response.setReceiveSiteCode(sendDetail.getReceiveSiteCode());
				response.setOperateTime(sendDetail.getOperateTime());
				sendResponseList.add(response);
			}
		}
		return sendResponseList;
	}
	
	@SuppressWarnings("unchecked")
	public List<SendDetail> findBySendCodeAndDmsCode(Map<String, Object> params) {
		return this.getSqlSessionRead().selectList(SendDatailReadDao.namespace + ".findBySendCode", params);
	}


}