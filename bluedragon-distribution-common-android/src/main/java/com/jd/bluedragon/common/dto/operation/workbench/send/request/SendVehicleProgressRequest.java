package com.jd.bluedragon.common.dto.operation.workbench.send.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @ClassName SendVehicleProgressRequest
 * @Description
 * @Author wyh
 * @Date 2022/5/19 16:08
 **/
public class SendVehicleProgressRequest implements Serializable {

    private static final long serialVersionUID = -6727195068356214322L;

    private User user;

    private CurrentOperate currentOperate;

    /**
     * send_vehicle业务主键
     */
    private String sendVehicleBizId;

    /**
     * 车牌号
     */
    private String vehicleNumber;
}
