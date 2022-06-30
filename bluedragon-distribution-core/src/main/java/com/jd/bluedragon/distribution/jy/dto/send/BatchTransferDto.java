package com.jd.bluedragon.distribution.jy.dto.send;

import lombok.Data;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

@Data
public class BatchTransferDto implements Serializable {
    private static final long serialVersionUID = 7753170373197145796L;
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
    private List<String> sendCodeList;
}
