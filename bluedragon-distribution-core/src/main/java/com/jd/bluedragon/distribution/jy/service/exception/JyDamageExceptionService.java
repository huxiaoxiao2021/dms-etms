package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpDamageDetailReq;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/25 20:18
 * @Description:异常-破损 service
 */
public interface JyDamageExceptionService {
    JdCResponse<Boolean> processTaskOfDamage(ExpDamageDetailReq req);
}
