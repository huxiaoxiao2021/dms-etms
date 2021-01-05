package com.jd.bluedragon.distribution.external.sdk.api.batch;

import com.jd.bluedragon.distribution.external.sdk.base.ServiceResult;
import com.jd.bluedragon.distribution.external.sdk.dto.batch.SendCodeReq;

/**
 * @ClassName IDMSSendCodeApi
 * @Description 批次号对外服务
 * @Author wyh
 * @Date 2021/1/4 15:54
 **/
public interface IDMSSendCodeApi {

    /**
     * 批次号生成服务，按业务方向给调用放分配系统标识
     * @see com.jd.bluedragon.distribution.external.sdk.constants.SysSourceEnum
     * @param request
     * @return
     */
    ServiceResult<String> genSendCode(SendCodeReq request);

}
