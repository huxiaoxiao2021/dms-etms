package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpContrabandReq;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionContrabandDto;

/**
 * @Author: ext.xuwenrui
 * @Date: 2023/8/15 10:18
 * @Description: 违禁品上报
 */

public interface JyContrabandExceptionService {
    JdCResponse<Boolean> processTaskOfContraband(ExpContrabandReq req);

    /**
     * 处理违禁品上报数据
     * @param dto
     * @throws InterruptedException
     */
    void  dealContrabandUploadData(JyExceptionContrabandDto dto) throws InterruptedException;
}
