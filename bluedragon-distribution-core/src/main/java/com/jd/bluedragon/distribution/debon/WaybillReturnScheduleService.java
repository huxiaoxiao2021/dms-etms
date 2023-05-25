package com.jd.bluedragon.distribution.debon;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.jsf.domain.ReturnScheduleRequest;
import com.jd.bluedragon.distribution.jsf.domain.ReturnScheduleResult;
import com.jd.ql.dms.common.domain.JdResponse;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/19 17:20
 * @Description: 德邦返调度服务
 */
public interface WaybillReturnScheduleService {

    /**
     * 根据运单信息进行返调度
     * @param request
     * @return
     */
    JdResponse<ReturnScheduleResult> returnScheduleSiteInfoByWaybill(ReturnScheduleRequest request);
}
