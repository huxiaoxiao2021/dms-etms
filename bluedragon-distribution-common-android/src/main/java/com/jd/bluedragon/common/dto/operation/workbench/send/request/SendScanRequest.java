package com.jd.bluedragon.common.dto.operation.workbench.send.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @ClassName SendScanRequest
 * @Description
 * @Author wyh
 * @Date 2022/5/19 22:15
 **/
public class SendScanRequest implements Serializable {

    private static final long serialVersionUID = -6891254799862705700L;

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

    private String barCode;

    /**
     * 跳过拦截强制提交
     */
    private Boolean forceSubmit;

    /**
     * 任务组号
     */
    private String groupCode;
}
