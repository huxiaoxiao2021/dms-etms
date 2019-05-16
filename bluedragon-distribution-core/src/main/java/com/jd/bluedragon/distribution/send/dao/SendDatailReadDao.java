package com.jd.bluedragon.distribution.send.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.api.response.SendBoxDetailResponse;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.utils.SerialRuleUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendDatailReadDao extends BaseDao<SendDetail> {

    public static final String namespace = SendDatailReadDao.class.getName();

    @SuppressWarnings("unchecked")
    public List<SendDetail> findUpdatewaybillCodeMessage(List<Long> queryCondition) {

        if (queryCondition == null || queryCondition.isEmpty()){
            return null;
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("queueIds", queryCondition);

        return this.getSqlSessionRead().selectList(
                SendDatailReadDao.namespace + ".findUpdatewaybillCodeMessage", paramMap);
    }

    /**
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

    /**
     * @param boxCode
     * @param createSiteCode
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<String> getWaybillCodesByBoxCodeAndFetchNum(String boxCode, Integer createSiteCode, Integer fetchNum) {
        if (boxCode == null || boxCode.isEmpty())
            return null;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("boxCode", boxCode);
        paramMap.put("fetchNum",fetchNum);
        if (null != createSiteCode && createSiteCode > 0)
            paramMap.put("createSiteCode", createSiteCode);
        return this.getSqlSessionRead().selectList(SendDatailReadDao.namespace + ".getWaybillCodesByBoxCodeAndFetchNum", paramMap);
    }

    /**
     * 根据包裹号查询当前分拣中心的send_d
     * @param waybillCode
     * @param createSiteCode
     * @return
     */
    public List<SendDetail> findSendByPackageCode(String waybillCode, Integer createSiteCode) {
        if (waybillCode == null || waybillCode.isEmpty()) {
            return null;
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("packageCode", waybillCode);
        paramMap.put("createSiteCode", createSiteCode);
        List<SendDetail> sendDetailList = this.getSqlSessionRead().selectList(SendDatailReadDao.namespace + ".findSendByPackageCode", paramMap);

        return sendDetailList;
    }

    @SuppressWarnings("unchecked")
    public List<SendBoxDetailResponse> findSendBoxByWaybillCode(String waybillCode) {
        if (waybillCode == null || waybillCode.isEmpty()) {
            return null;
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("waybillCode", waybillCode);
        List<SendDetail> sendDetailList = this.getSqlSessionRead().selectList(
                SendDatailReadDao.namespace + ".findSendBoxByWaybillCode", paramMap);

        List<SendBoxDetailResponse> sendResponseList = new ArrayList<SendBoxDetailResponse>();
        if (sendDetailList != null && sendDetailList.size() > 0) {
            for (SendDetail sendDetail : sendDetailList) {
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

    @SuppressWarnings("unchecked")
    public List<SendDetail> querySendDetailBySendCodes_SELF(List<String> sendCodes) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sendCodeList", sendCodes);
        List<Integer> dmsCodes = new ArrayList<Integer>();
        for (String sendCode : sendCodes) {
            Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCode);
            if (null != createSiteCode)
                dmsCodes.add(createSiteCode);
        }
        params.put("dmsList", dmsCodes);
        return this.getSqlSessionRead().selectList(SendDatailReadDao.namespace + ".querySendDetail_SELF", params);
    }

    @SuppressWarnings("unchecked")
    public List<SendDetail> querySendDetailBySendCodes_3PL(List<String> sendCodes) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sendCodeList", sendCodes);
        List<Integer> dmsCodes = new ArrayList<Integer>();
        for (String sendCode : sendCodes) {
            Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCode);
            if (null != createSiteCode)
                dmsCodes.add(createSiteCode);
        }
        params.put("dmsList", dmsCodes);
        return this.getSqlSessionRead().selectList(SendDatailReadDao.namespace + ".querySendDetail_3PL", params);
    }

    @SuppressWarnings("unchecked")
    public List<SendDetail> queryBySendCodeAndSiteCode(String sendCode, Integer createSiteCode, Integer receiveSiteCode, Integer senddStatus){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sendCode", sendCode);
        params.put("createSiteCode", createSiteCode);
        params.put("receiveSiteCode", receiveSiteCode);
        params.put("senddStatus", senddStatus);
        return this.getSqlSessionRead().selectList(SendDatailReadDao.namespace + ".queryBySendCodeAndSiteCode", params);
    }

    @SuppressWarnings("unchecked")
    public List<SendDetail> querySendDSimpleInfoBySendCode(String sendCode, Integer createSiteCode) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sendCode", sendCode);
        params.put("createSiteCode", createSiteCode);
        return this.getSqlSessionRead().selectList(SendDatailReadDao.namespace + ".querySendDSimpleInfoBySendCode", params);
    }

}