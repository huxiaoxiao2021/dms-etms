package com.jd.bluedragon.distribution.send.service;

import com.jd.bluedragon.distribution.send.domain.SendDetail;

import java.util.List;

public interface SendDetailService {

    /**
     * 根据包裹号查询当前分拣中心的send_d
     * @param waybillCode
     * @param createSiteCode
     * @return
     */
    public List<SendDetail> findSendByPackageCodeFromReadDao(String waybillCode, Integer createSiteCode);
}
