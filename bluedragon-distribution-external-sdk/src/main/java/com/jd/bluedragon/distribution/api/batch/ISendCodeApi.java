package com.jd.bluedragon.distribution.api.batch;

import com.jd.bluedragon.distribution.base.ServiceResult;
import com.jd.bluedragon.distribution.dto.batch.SendCodeReq;

/**
 * @ClassName ISendCodeApi
 * @Description 批次号对外服务
 * @Author wyh
 * @Date 2021/1/4 15:54
 **/
public interface ISendCodeApi {

    /**
     * 批次号生成服务，按业务方向给调用放分配系统标识
     * @see com.jd.bluedragon.distribution.constants.SysSourceEnum
     * @param request
     * @return
     */
    ServiceResult<String> genSendCode(SendCodeReq request);

}
