package com.jd.bluedragon.distribution.jy.dto.send;

import com.jd.bluedragon.distribution.jy.dto.JyReqBaseDto;

import java.io.Serializable;

/**
 * @ClassName SendCheckRequest
 * @Description
 * @Author wyh
 * @Date 2022/6/12 20:12
 **/
public class SendCheckReq extends JyReqBaseDto implements Serializable {

    private static final long serialVersionUID = 6805805124277289588L;

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

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public Boolean getForceSubmit() {
        return forceSubmit;
    }

    public void setForceSubmit(Boolean forceSubmit) {
        this.forceSubmit = forceSubmit;
    }

    public Boolean getSendForWholeBoard() {
        return sendForWholeBoard;
    }

    public void setSendForWholeBoard(Boolean sendForWholeBoard) {
        this.sendForWholeBoard = sendForWholeBoard;
    }

    public Boolean getNoTaskConfirmDest() {
        return noTaskConfirmDest;
    }

    public void setNoTaskConfirmDest(Boolean noTaskConfirmDest) {
        this.noTaskConfirmDest = noTaskConfirmDest;
    }

    public String getNoTaskRemark() {
        return noTaskRemark;
    }

    public void setNoTaskRemark(String noTaskRemark) {
        this.noTaskRemark = noTaskRemark;
    }

    public Long getConfirmSendDestId() {
        return confirmSendDestId;
    }

    public void setConfirmSendDestId(Long confirmSendDestId) {
        this.confirmSendDestId = confirmSendDestId;
    }
}
