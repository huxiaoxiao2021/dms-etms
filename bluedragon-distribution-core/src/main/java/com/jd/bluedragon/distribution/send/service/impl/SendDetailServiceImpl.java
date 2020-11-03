package com.jd.bluedragon.distribution.send.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.dao.SendDatailReadDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.dto.SendDetailDto;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("sendDetailService")
public class SendDetailServiceImpl implements SendDetailService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

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
    @Override
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
        log.debug("SendDetailServiceImpl.findPageSendDetail begin...");
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

    /**
     * 根据批次号查询 包裹号
     * @param params
     * @return
     */
    @Override
    public List<String> queryPackageCodeBySendCode(SendDetailDto params){
        return sendDatailDao.queryPackageCodeBySendCode(params);
    }

    /**
     * 根据箱号号查询 包裹号
     * @param params
     * @return
     */
    @Override
    public List<String> queryPackageCodeByboxCode(SendDetailDto params){
        return sendDatailDao.queryPackageCodeByboxCode(params);
    }

    /**
     * 根据运单号查询 包裹号
     * @param params
     * @return
     */
    @Override
    public List<String> queryPackageByWaybillCode(SendDetailDto params){
        return sendDatailDao.queryPackageByWaybillCode(params);
    }

    @Override
    public boolean checkSendIsExist(String sendCode) {
        Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCode);
        if(null == createSiteCode){
            log.error("checkSendIsExist-->参数sendCode:{}",sendCode);
            return false;
        }

        SendDetail queryDetail = new SendDetail();
        queryDetail.setSendCode(sendCode);
        queryDetail.setCreateSiteCode(createSiteCode);

        //查询存在
        return sendDatailDao.querySendBySiteCodeAndSendCode(queryDetail) != null;

    }
}
