package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.api.response.ContainerRelationResponse;
import com.jd.bluedragon.distribution.api.response.DeparturePrintResponse;

/**
 * 发往物流网关的接口不要在此类中加方法
 * <p>
 * Created by lixin39 on 2018/11/9.
 */
public interface DmsReceiveService {

    /**
     * 干线收货发送发车信息到计费系统（通过MQ）
     *
     * @param type 是根据车次号还是包裹号查询
     * @param code 车次号或者包裹号
     * @return
     */
    DeparturePrintResponse queryArteryBillInfo(String type, String code, Integer dmsID, String dmsName, String userCode, String userName);

    /**
     * 根据编号获得对应的关系,根据编号获取最近生成的关系(kvindex里获取对应关系)
     *
     * @param containerCode
     * @return
     */
    ContainerRelationResponse getBoxCodeByContainerCode(String containerCode, Integer siteCode);

}
