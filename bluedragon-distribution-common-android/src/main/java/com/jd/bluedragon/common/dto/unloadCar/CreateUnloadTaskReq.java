package com.jd.bluedragon.common.dto.unloadCar;

import java.io.Serializable;

/**
 * @program: bluedragon-distribution
 * @description: 创建无封车编号任务请求参数
 * @author: wuming
 * @create: 2020-12-23 11:34
 */
public class CreateUnloadTaskReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操作人姓名
     */
    private String operateUserName;

    /**
     * 操作人erp
     */
    private String operateUserErp;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 操作人所属站点编码
     */
    private Long createSiteCode;

    public CreateUnloadTaskReq() {
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Long getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Long createSiteCode) {
        this.createSiteCode = createSiteCode;
    }
}
