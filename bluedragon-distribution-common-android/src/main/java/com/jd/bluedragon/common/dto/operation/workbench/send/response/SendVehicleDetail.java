package com.jd.bluedragon.common.dto.operation.workbench.send.response;

/**
 * @ClassName SendVehicleD
 * @Description
 * @Author wyh
 * @Date 2022/5/18 18:22
 **/
public class SendVehicleDetail extends BaseSendVehicleDetail{

    private static final long serialVersionUID = -800277887131111L;

    /**
     * 状态
     */
    private Integer itemStatus;

    /**
     * 状态描述
     */
    private String itemStatusDesc;
}
