package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.api.request.client.DeviceLocationUploadPo;

/**
 * 设备位置网关服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-11-20 19:59:10 周日
 */
public interface DeviceLocationGatewayService {

    /**
     * 上传设备位置信息
     * @param deviceLocationUploadPO 位置信息
     * @return 上传结果
     * @author fanggang7
     * @time 2022-11-23 18:21:59 周三
     */
    JdCResponse<Boolean> uploadLocationInfo(DeviceLocationUploadPo deviceLocationUploadPO);

    /**
     * 检查用户位置是否与其所属站点匹配
     * @param deviceLocationUploadPo 上传信息
     * @return 匹配结果
     * @author fanggang7
     * @time 2022-11-23 18:21:59 周三
     */
    JdCResponse<Boolean> checkLocationMatchUserSite(DeviceLocationUploadPo deviceLocationUploadPo);

}
