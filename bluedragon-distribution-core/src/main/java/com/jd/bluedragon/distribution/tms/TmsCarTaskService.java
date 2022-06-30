package com.jd.bluedragon.distribution.tms;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.carTask.request.CarTaskQueryRequest;
import com.jd.bluedragon.common.dto.carTask.request.CarTaskUpdateDto;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskEndNodeResponse;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskResponse;

import java.util.List;

public interface TmsCarTaskService {


    /**
     * 根据当前站点获取可查询运输车辆任务的目的站点列表
     * @param startNodeCode
     */
    JdCResponse<List<CarTaskEndNodeResponse>> getEndNodeList(String startNodeCode);


    /**
     * 获取运输任务列表
     * @param queryRequest
     * @return
     */
    JdCResponse<List<CarTaskResponse>> queryCarTaskList(CarTaskQueryRequest queryRequest);

    /**
     * 运输任务更新接口
     * @param updateDto
     * @return
     */
    JdCResponse updateCarTaskInfo(CarTaskUpdateDto updateDto);


}
