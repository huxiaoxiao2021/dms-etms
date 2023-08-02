package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req.AviationSendTaskListReq;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res.AviationSendTaskListRes;
import com.jd.bluedragon.common.dto.select.SelectOption;

import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 14:13
 * @Description
 */


public interface JyAviationRailwaySendSealGatewayService {

    /**
     * 发货扫描方式枚举
     * @return
     */
    JdCResponse<List<SelectOption>> scanTypeOptions();

    /**
     * 分页获取-航空发货任务列表数据
     * @param request
     * @return
     */
    JdCResponse<AviationSendTaskListRes> pageFetchAviationSendTaskList(AviationSendTaskListReq request);


}
