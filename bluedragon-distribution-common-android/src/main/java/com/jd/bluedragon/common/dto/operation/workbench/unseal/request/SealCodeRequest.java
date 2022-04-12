package com.jd.bluedragon.common.dto.operation.workbench.unseal.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @ClassName SealCodeRequest
 * @Description
 * @Author wyh
 * @Date 2022/3/22 10:30
 **/
public class SealCodeRequest implements Serializable {

    private static final long serialVersionUID = 5169641497849661081L;

    private User user;

    private CurrentOperate currentOperate;

    /**
     * 封车编码
     */
    private String sealCarCode;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 操作站点Id
     */
    private Integer desealSiteId;

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

    public Integer getDesealSiteId() {
        return desealSiteId;
    }

    public void setDesealSiteId(Integer desealSiteId) {
        this.desealSiteId = desealSiteId;
    }
}
