package com.jd.bluedragon.distribution.resident.service;

import com.jd.bluedragon.distribution.api.request.PopPrintRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.resident.domain.ResidentCollectDto;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2021/10/20 6:37 下午
 */
public interface ResidentCollectService {

    /**
     * 驻厂揽收
     *
     * @param residentCollectDto
     * @return
     */
    InvokeResult<Boolean> residentCollect(ResidentCollectDto residentCollectDto);

    /**
     * 驻厂揽收完成
     *
     * @param popPrintRequest
     * @return
     */
    InvokeResult<Boolean> afterCollectFinish(PopPrintRequest popPrintRequest);
}
