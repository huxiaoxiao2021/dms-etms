package com.jd.bluedragon.common.dto.operation.workbench.send.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * 待扫提示信息
 * @author fanggang7
 * @time 2023-08-15 16:16:04 周二
 */
public class SendVehicleToScanTipsRequest implements Serializable {

    private static final long serialVersionUID = -3313588752534522690L;

    public static Integer queryType_REFRESH = 1;

    public static Integer queryType_SEND_FINISH = 1;

    private User user;

    private CurrentOperate currentOperate;

    /**
     * 发货任务主键
     */
    private String sendVehicleBizId;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * send_detail业务主键
     */
    private String sendDetailBizId;

    private Integer queryType;

    public SendVehicleToScanTipsRequest() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getSendDetailBizId() {
        return sendDetailBizId;
    }

    public void setSendDetailBizId(String sendDetailBizId) {
        this.sendDetailBizId = sendDetailBizId;
    }

    public Integer getQueryType() {
        return queryType;
    }

    public void setQueryType(Integer queryType) {
        this.queryType = queryType;
    }
}
