package com.jd.bluedragon.distribution.tms;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.carTask.request.CarTaskQueryRequest;
import com.jd.bluedragon.common.dto.carTask.request.CarTaskUpdateDto;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskEndNodeResponse;
import com.jd.bluedragon.common.dto.carTask.response.CarTaskResponse;
import com.jd.tms.tpc.dto.LineCargoVolumeDetailDto;
import com.jd.tms.tpc.dto.LineCargoVolumeQueryDto;
import com.jd.tms.tpc.dto.LineCargoVolumeUpdateDto;

import java.util.List;

public interface TmsCarTaskService {


    /**
     * 根据始发分拣网点ID获取 目的分拣网点列表
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
