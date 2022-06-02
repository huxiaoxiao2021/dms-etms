package com.jd.bluedragon.common.dto.operation.workbench.send.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @ClassName SendAbnormalPackRequest
 * @Description
 * @Author wyh
 * @Date 2022/5/19 17:55
 **/
public class SendAbnormalPackRequest implements Serializable {

    private static final long serialVersionUID = -7121343145796741992L;

    private User user;

    private CurrentOperate currentOperate;

    private Integer pageNumber;

    private Integer pageSize;

    /**
     * send_vehicle业务主键
     */
    private String sendVehicleBizId;

    /**
     * 车牌号
     */
    private String vehicleNumber;
}
