package com.jd.bluedragon.common.dto.operation.workbench.unload.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @ClassName UnloadCommonRequest
 * @Description
 * @Author wyh
 * @Date 2022/3/31 20:52
 **/
public class UnloadCommonRequest implements Serializable {

    private static final long serialVersionUID = 8579173836769728478L;

    private User user;

    private CurrentOperate currentOperate;

    /**
     * 业务主键
     */
    private String bizId;

    /**
     * 封车编码
     */
    private String sealCarCode;

    /**
     * 车牌号
     */
    private String vehicleNumber;

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

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
}
