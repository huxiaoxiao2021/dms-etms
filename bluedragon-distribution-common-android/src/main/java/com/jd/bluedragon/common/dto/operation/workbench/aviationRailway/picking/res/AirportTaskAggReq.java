package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.res;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class AirportTaskAggReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = -2083002886333203311L;
    /**
     * 机场/车站编码
     */
    private String pickingNodeCode;
    /**
     * 是否无任务分组
     */
    private Boolean noTaskFlag;
    /**
     * 任务状态
     * @see com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodStatusEnum
     */
    private Integer status;
    /**
     * 提货任务类型
     * @see com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodTaskTypeEnum
     */
    private Integer taskType;

    private Integer pageNum;

    private Integer pageSize;
}
