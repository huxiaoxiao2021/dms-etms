package com.jd.bluedragon.common.dto.send.response;

import java.io.Serializable;
import java.util.List;

public class VehicleTaskResp implements Serializable {
    /**
     * 主任务相关信息
     */
    private String bizId;
    private String transWorkCode;
    private String vehicleNumber;
    private Integer vehicleStatus;
    private Integer transWay;
    private String transWayName;
    private Integer vehicleType;
    private String vehicleTypeName;
    private Integer lineType;
    private String lineTypeName;
    /**
     * 子任务列表
     */
    List<VehicleDetailTaskDto> vehicleDetailTaskDtoList;
}
