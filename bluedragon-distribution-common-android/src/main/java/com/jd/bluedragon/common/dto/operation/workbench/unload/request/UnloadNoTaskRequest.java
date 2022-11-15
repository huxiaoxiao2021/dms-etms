package com.jd.bluedragon.common.dto.operation.workbench.unload.request;

import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * 无任务卸车请求
 *
 * @author hujiping
 * @date 2022/4/8 4:58 PM
 */
public class UnloadNoTaskRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    // 操作人场地ID
    private Integer operateSiteId;
    // 操作人场地名称
    private String operateSiteName;
    // 车牌号
    private String vehicleNumber;

    private User user;

    public Integer getOperateSiteId() {
        return operateSiteId;
    }

    public void setOperateSiteId(Integer operateSiteId) {
        this.operateSiteId = operateSiteId;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
