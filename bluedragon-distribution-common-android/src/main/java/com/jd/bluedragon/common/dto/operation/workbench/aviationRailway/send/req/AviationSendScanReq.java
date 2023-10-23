package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.ValidateIgnore;

import java.io.Serializable;

/**
 * @author liwenji
 * @description 发货扫描
 * @date 2023-08-15 18:03
 */
public class AviationSendScanReq  extends BaseReq implements Serializable {
    
    private static final long serialVersionUID = -6891254799862705090L;

    /**
     * send_vehicle业务主键
     */
    private String sendVehicleBizId;

    /**
     * send_vehicle_detail业务主键
     */
    private String sendVehicleDetailBizId;

    private String barCode;

    /**
     * 扫描单号类型
     */
    private Integer barCodeType;

    /**
     * 集包袋号
     */
    private String materialCode;
    

    private String taskId;

    /**
     * 跳过发货拦截强制提交
     */
    private Boolean forceSubmit;

    /**
     * 用户确认的发货目的地
     */
    private Long confirmSendDestId;

    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getBarCodeType() {
        return barCodeType;
    }

    public void setBarCodeType(Integer barCodeType) {
        this.barCodeType = barCodeType;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }
    

    public Boolean getForceSubmit() {
        return forceSubmit;
    }

    public void setForceSubmit(Boolean forceSubmit) {
        this.forceSubmit = forceSubmit;
    }
    

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    
    public String getSendVehicleDetailBizId() {
        return sendVehicleDetailBizId;
    }

    public void setSendVehicleDetailBizId(String sendVehicleDetailBizId) {
        this.sendVehicleDetailBizId = sendVehicleDetailBizId;
    }


    public Long getConfirmSendDestId() {
        return confirmSendDestId;
    }

    public void setConfirmSendDestId(Long confirmSendDestId) {
        this.confirmSendDestId = confirmSendDestId;
    }
}
