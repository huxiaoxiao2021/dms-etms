package com.jd.bluedragon.external.gateway.blockcar;

import com.jd.bluedragon.common.dto.blockcar.request.CancelPreBlockCarRequest;
import com.jd.ql.dms.common.domain.JdRequest;
import com.jd.ql.dms.common.domain.JdResponse;

/**
 * 取消预封车
 * @author zhangzhongkai8
 * @date 2020/11/18  20:24
 */
public interface CancelPreBlockCarGatewayExternalService {

    /**
     * 取消预封车
     * @param request
     * @return
     */
    public JdResponse<Boolean> cancelPreBlockCar(CancelPreBlockCarRequest request);
}
