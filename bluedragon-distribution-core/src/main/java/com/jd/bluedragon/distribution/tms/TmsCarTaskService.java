package com.jd.bluedragon.distribution.tms;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskEndNodeResponse;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskResponse;

import java.util.List;

public interface TmsCarTaskService {


    /**
     * 根据始发分拣网点ID获取 目的分拣网点列表
     * @param startNodeCode
     */
    JdCResponse<List<CarTaskEndNodeResponse>> getEndNodeList(String startNodeCode);


    /**
     * 根据始发网点和目的网点查询车辆任务
     * @param startNodeCode
     * @param endNodeCode
     */
    JdCResponse<List<CarTaskResponse>> queryCarTaskList(String startNodeCode, String endNodeCode);
}
