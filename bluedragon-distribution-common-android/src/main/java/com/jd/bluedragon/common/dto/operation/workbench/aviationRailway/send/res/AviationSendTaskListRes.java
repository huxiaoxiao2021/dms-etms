package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleStatusStatis;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 15:59
 * @Description
 */
public class AviationSendTaskListRes extends BaseReq implements Serializable {



    /**
     * 车辆状态数量统计
     */
    private List<TaskStatusStatistics> statusAgg;


    private AviationRailwaySendSealTaskData<Object> aviationToSendSealTaskData;
}
