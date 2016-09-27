package com.jd.bluedragon.distribution.send.dao;

import com.jd.bluedragon.distribution.api.response.SendBoxDetailResponse;
import com.jd.bluedragon.distribution.base.dao.KvIndexDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.utils.CollectionHelper;
import com.jd.bluedragon.utils.StringHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Created by dudong on 2016/9/26.
 */
public class SendDetailReadRouterDao extends SendDatailReadDao {

    private static final Log logger = LogFactory.getLog(SendDetailReadRouterDao.class);

    @Autowired
    private KvIndexDao kvIndexDao;

//    @Override
//    public List<SendDetail> findUpdatewaybillCodeMessage(List<String> queryCondition) {
//        return super.findUpdatewaybillCodeMessage(queryCondition);
//    }
//
//    @Override
//    public List<String> findWaybillByBoxCode(String boxCode) {  //// FIXME: 2016/9/26
//        return super.findWaybillByBoxCode(boxCode);
//    }

    @Override
    public List<SendBoxDetailResponse> findSendBoxByWaybillCode(String waybillCode) {  //// FIXME: 2016/9/26
        if (waybillCode == null || waybillCode.isEmpty()) {
            return null;
        }
        try {
            List<SendBoxDetailResponse> sendResponseList = new ArrayList<SendBoxDetailResponse>();
            List<Integer> createSiteCodes = kvIndexDao.queryCreateSiteCodesByKey(waybillCode);
            if (null == createSiteCodes || createSiteCodes.size() <= 0) {
                return super.findSendBoxByWaybillCode(waybillCode);
            }
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("waybillCode", waybillCode);
            paramMap.put("createSiteCode", StringHelper.join(createSiteCodes,","));
            List<SendDetail> sendDetailList = this.getSqlSessionRead().selectList(
                    SendDatailReadDao.namespace + ".findSendBoxByWaybillCode", paramMap);

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
                return sendResponseList;
            }
        } catch (Throwable e) {
            logger.error("获取分库拆分键失败，运单号" + waybillCode, e);
        }

        return super.findSendBoxByWaybillCode(waybillCode);
    }

//    @Override
//    public List<SendDetail> findBySendCodeAndDmsCode(Map<String, Object> params) {
//        return super.findBySendCodeAndDmsCode(params);
//    }
}
