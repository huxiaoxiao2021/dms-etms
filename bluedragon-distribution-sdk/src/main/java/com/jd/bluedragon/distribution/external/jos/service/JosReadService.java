package com.jd.bluedragon.distribution.external.jos.service;

import com.jd.bluedragon.distribution.jsf.domain.WhemsWaybillResponse;

import java.util.List;

/**
 * Created by yangbo7 on 2015/9/1.
 */
public interface JosReadService {

    /**
     * 通过运单号拉取运单数据接口
     * @param request 运单号列表
     * @return
     */
    public WhemsWaybillResponse getWhemsWaybill(List<String> request);

}
