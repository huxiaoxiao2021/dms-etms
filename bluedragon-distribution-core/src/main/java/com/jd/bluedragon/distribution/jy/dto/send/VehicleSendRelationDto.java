package com.jd.bluedragon.distribution.jy.dto.send;

import lombok.Data;

import java.util.List;
@Data
public class VehicleSendRelationDto {
    private List<String> sendCodes;
    /**
     * 迁移前的主任务编号
     */
    private String fromSendVehicleBizId;
    /**
     * 迁移前的子任务编号
     */
    private String fromSendVehicleDetailBizId;

    /**
     * 迁移后的主任务编号
     */
    private String toSendVehicleBizId;
    /**
     * 迁移后的子任务编号
     */
    private String toSendVehicleDetailBizId;

    private String updateUserErp;
    private String updateUserName;
    private Integer updateUserCode;
    private Boolean sameWayFlag;
    private String newSendCode;
    private Long createSiteId;
}
