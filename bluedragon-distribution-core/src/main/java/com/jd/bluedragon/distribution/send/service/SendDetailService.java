package com.jd.bluedragon.distribution.send.service;

import com.jd.bluedragon.distribution.send.domain.SendDetail;

import java.util.List;
import java.util.Map;

public interface SendDetailService {

    /**
     * 根据包裹号查询当前分拣中心的send_d
     * @param waybillCode
     * @param createSiteCode
     * @return
     */
    public List<SendDetail> findSendByPackageCodeFromReadDao(String waybillCode, Integer createSiteCode);

    /**
     * 根据运单号或者包裹号，当前站点查询所有已发货记录
     * @param createSiteCode
     * @param waybillCode
     * @param packageCode
     * @return
     */
    public List<SendDetail> findByWaybillCodeOrPackageCode(Integer createSiteCode,String waybillCode, String packageCode);

    SendDetail findOneByWaybillCode(Integer createSiteCode,String waybillCode);

    /**分页查询发货记录*/
    public List<SendDetail> findPageSendDetail(Map<String,Object> params);
    public Integer querySendDCountBySendCode(String sendCode);
}
