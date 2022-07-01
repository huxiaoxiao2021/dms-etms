package com.jd.bluedragon.distribution.send.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.SendMService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service("sendMService")
public class SendMServiceImpl implements SendMService{

    @Autowired
    private SendMDao sendMDao;

    @Override
    public List<SendM> findDeliveryRecord(Integer createSiteCode, String boxCode) {
        if(StringUtils.isEmpty(boxCode) || createSiteCode == null){
            return Collections.emptyList();
        }
        SendM queryPara = new SendM();
        queryPara.setBoxCode(boxCode);
        queryPara.setCreateSiteCode(createSiteCode);
        //查询箱子发货记录
        return sendMDao.selectBySendSiteCode(queryPara);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SendMServiceImpl.findAllSendCodesWithStartTime", jAppName=Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP, JProEnum.FunctionError})
    public List<SendM> findAllSendCodesWithStartTime(Integer createSiteCode, Integer receiveSiteCode, Date startDate) {
        return sendMDao.findAllSendCodesWithStartTime(createSiteCode, receiveSiteCode, startDate);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SendMServiceImpl.batchSearchBySiteCodeAndStartTime", jAppName=Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP, JProEnum.FunctionError})
    public List<SendM> batchSearchBySiteCodeAndStartTime(Integer createSiteCode, List<Integer> receiveSiteCodes, Date startDate, Date endDate) {
        return sendMDao.batchSearchBySiteCodeAndStartTime(createSiteCode, receiveSiteCodes, startDate, endDate);
    }

    @Override
    public List<SendM> findByParams(SendM params) {
        if (params == null
                || (StringUtils.isEmpty(params.getBoxCode()) && StringUtils.isEmpty(params.getBoardCode()))
                || params.getCreateSiteCode() == null) {
            return Collections.emptyList();
        }
        //查询箱子发货记录
        return sendMDao.selectBySendSiteCode(params);
    }


    /**
     * 根据始发分拣中心、目的分拣中心、箱号确定send_m的一条发货记录
     * @param createSiteCode
     * @param boardCode
     * @param sendmStatus
     * @return
     */
    @Override
    public SendM selectSendByBoardCode(Integer createSiteCode, String boardCode, Integer sendmStatus){
        return sendMDao.selectSendByBoardCode(createSiteCode, boardCode, sendmStatus);
    }

    @Override
    public String querySendCodeBySelective(SendM sendM) {
        return sendMDao.querySendCodeBySelective(sendM);
    }

    @Override
    public List<SendM> selectBySiteAndSendCode(Integer createSiteCode, String sendCode) {
        return sendMDao.selectBySiteAndSendCode(createSiteCode,sendCode);
    }
}
