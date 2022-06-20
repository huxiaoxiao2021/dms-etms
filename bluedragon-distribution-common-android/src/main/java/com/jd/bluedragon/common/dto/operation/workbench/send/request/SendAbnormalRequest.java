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
public class SendAbnormalRequest implements Serializable {

    private static final long serialVersionUID = -7121343145796741992L;

    private User user;

    private CurrentOperate currentOperate;

    /**
     * send_detail业务主键
     */
    private String sendDetailBizId;

    /**
     * 发货任务主键
     */
    private String sendVehicleBizId;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 是否强制前往封车 默认：false
     */
    private Boolean forceGoToSeal = false;

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

    public String getSendDetailBizId() {
        return sendDetailBizId;
    }

    public void setSendDetailBizId(String sendDetailBizId) {
        this.sendDetailBizId = sendDetailBizId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Boolean getForceGoToSeal() {
        return forceGoToSeal;
    }

    public void setForceGoToSeal(Boolean forceGoToSeal) {
        this.forceGoToSeal = forceGoToSeal;
    }

    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }
}
