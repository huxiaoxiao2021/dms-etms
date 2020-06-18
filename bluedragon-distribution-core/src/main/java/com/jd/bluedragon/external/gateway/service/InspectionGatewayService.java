package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.inspection.response.InspectionResultDto;

/**
 * 验货相关
 * 发布到物流网关 由安卓调用
 * @author : xumigen
 * @date : 2019/6/14
 */
public interface InspectionGatewayService {

    JdCResponse<InspectionResultDto> getStorageCode(String packageBarOrWaybillCode, Integer siteCode);

    /* 运单是否存在待确认的包装任务 */
    JdCResponse<Boolean> isExistConsumableRecord (String packageBarOrWaybillCode);
}
