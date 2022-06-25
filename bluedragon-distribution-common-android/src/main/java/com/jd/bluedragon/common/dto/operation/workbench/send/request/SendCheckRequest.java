package com.jd.bluedragon.common.dto.operation.workbench.send.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @ClassName SendCheckRequest
 * @Description
 * @Author wyh
 * @Date 2022/6/12 20:12
 **/
public class SendCheckRequest implements Serializable {

    private static final long serialVersionUID = 6805805124277289588L;

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
     * 扫描单号类型
     */
    private Integer barCodeType;

    /**
     * 集包袋号
     */
    private String materialCode;

    /**
     * 任务组号
     */
    private String groupCode;

    /**
     * 跳过发货拦截强制提交
     */
    private Boolean forceSubmit;

    /**
     * 是否发送整板
     */
    private Boolean sendForWholeBoard;

    /**
     * 无任务发货确认目的地
     */
    private Boolean noTaskConfirmDest;

    /**
     * 无任务首次发货备注
     */
    private String noTaskRemark;

    /**
     * 用户确认的发货目的地
     */
    private Long confirmSendDestId;
}
