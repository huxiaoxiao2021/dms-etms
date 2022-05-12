package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.carTask.request.CarTaskQueryRequest;
import com.jd.bluedragon.common.dto.carTask.request.CarTaskUpdateDto;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskEndNodeResponse;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskResponse;

import java.util.List;

public interface TMSCarTaskGateWayService {



    /**
     * 根据始发分拣网点ID获取 目的分拣网点列表
     * @param startNodeCode
     */
    JdCResponse<List<CarTaskEndNodeResponse>> getEndNodeList(String startNodeCode);

    /**
     * 根据始发网点和目的网点查询车辆任务
     * @param request
     */
    JdCResponse<List<CarTaskResponse>> queryCarTaskList(CarTaskQueryRequest request);

    /**
     * 更新车辆任务信息
     * @param carTaskUpdateDto
     * @return
     */
    JdCResponse updateCarTaskInfo(CarTaskUpdateDto carTaskUpdateDto);
}
