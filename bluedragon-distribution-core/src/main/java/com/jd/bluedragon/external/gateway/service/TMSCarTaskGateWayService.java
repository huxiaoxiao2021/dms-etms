package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskEndNodeResponse;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskResponse;

import java.util.List;

public interface TMSCarTaskGateWayService {



    /**
     * 根据始发分拣网点ID获取 目的分拣网点列表
     * @param beginNodeCode
     */
    JdCResponse<List<CarTaskEndNodeResponse>> getEndNodeList(Integer beginNodeCode);


    /**
     * 根据始发网点和目的网点查询车辆任务
     * @param beginNodeCode
     * @param endNodeCode
     */
    JdCResponse<List<CarTaskResponse>> queryCarTaskList(Integer beginNodeCode, Integer endNodeCode);

    void updateCarTaskInfo();
}
