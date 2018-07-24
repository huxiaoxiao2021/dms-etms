package com.jd.bluedragon.distribution.send.service;

import com.jd.bluedragon.distribution.send.domain.SendM;

import java.util.List;

public interface SendMService {

    /**
     * 查询当前分拣中心的所有发货记录
     * @param createSiteCode
     * @param boxCode
     * @return
     */
    public List<SendM> findDeliveryRecord(Integer createSiteCode, String boxCode);


}
