package com.jd.bluedragon.distribution.send.service.impl;

import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.dao.SendDatailReadDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.dto.SendDetailDto;
import com.jd.bluedragon.distribution.send.service.SendDetailService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("sendDetailService")
public class SendDetailServiceImpl implements SendDetailService {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private SendDatailReadDao sendDatailReadDao;

    @Autowired
    private SendDatailDao sendDatailDao;

    /**
     * 根据包裹号查询当前分拣中心的send_d
     * @param waybillCode
     * @param createSiteCode
     * @return
     */
    public List<SendDetail> findSendByPackageCodeFromReadDao(String waybillCode, Integer createSiteCode){
        return sendDatailReadDao.findSendByPackageCode(waybillCode, createSiteCode);
    }

    @Override
    public List<SendDetail> findByWaybillCodeOrPackageCode(Integer createSiteCode, String waybillCode, String packageCode) {
        SendDetail sendDetail = new SendDetail();
        sendDetail.setCreateSiteCode(createSiteCode);
        sendDetail.setPackageBarcode(packageCode);
        sendDetail.setWaybillCode(waybillCode);
        return sendDatailDao.findByWaybillCodeOrPackageCode(sendDetail);
    }

    @Override
    public SendDetail findOneByWaybillCode(Integer createSiteCode, String waybillCode) {
        SendDetail sendDetail = new SendDetail();
        sendDetail.setCreateSiteCode(createSiteCode);
        sendDetail.setWaybillCode(waybillCode);
        return sendDatailDao.findOneByWaybillCode(sendDetail);
    }

    @Override
    public List<SendDetail> findPageSendDetail(Map<String, Object> params) {
        logger.info("SendDetailServiceImpl.findPageSendDetail begin...");
        return sendDatailDao.findPageSendDetail(params);
    }

    @Override
    public List<SendDetail> findSendPageByParams(SendDetailDto params) {
        if (params != null && params.getCreateSiteCode() != null) {
            return sendDatailDao.findSendPageByParams(params);
        }
        return null;
    }
    @Override
    public Integer querySendDCountBySendCode(String sendCode) {
        return sendDatailDao.querySendDCountBySendCode(sendCode);
    }
}
