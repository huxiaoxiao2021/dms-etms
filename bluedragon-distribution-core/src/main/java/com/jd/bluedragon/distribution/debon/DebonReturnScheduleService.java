package com.jd.bluedragon.distribution.debon;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/19 17:20
 * @Description: 德邦返调度服务
 */
public interface DebonReturnScheduleService {

    /**
     * 根据运单信息进行返调度
     * @param request
     * @return
     */
    JdCResponse<Boolean> returnScheduleSiteInfoByWaybill(ReturnScheduleRequest request);
}
