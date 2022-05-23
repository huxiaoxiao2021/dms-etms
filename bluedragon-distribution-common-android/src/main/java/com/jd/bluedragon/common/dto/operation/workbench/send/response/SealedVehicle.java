package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.util.List;

/**
 * @ClassName SealedVehicle
 * @Description
 * @Author wyh
 * @Date 2022/5/18 19:49
 **/
public class SealedVehicle extends BaseSendVehicle {

    private static final long serialVersionUID = 8760156973684779457L;

    /**
     * 封签数
     */
    private Integer sealCodeCount;

    /**
     * 发货流向
     */
    private List<SendVehicleDetail> sendDestList;
}
