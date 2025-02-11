package com.jd.bluedragon.common.dto.operation.workbench.send.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.StevedoringMerchant;
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
     * send_vehicle_detail业务主键
     */
    private String sendVehicleDetailBizId;

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

    private String taskId;

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
     * 自定义任务名称
     */
    private String taskName;

    /**
     * 用户确认的发货目的地
     */
    private Long confirmSendDestId;

    /**
     * 忽略验证的条件
     */
    private ValidateIgnore validateIgnore;

    /**
     * 岗位类型
     * JyFuncCodeEnum
     */
    private String post;

    /**
     * 前置下一流向
     */
    private Long preNextSiteCode;

    /**
     * 装卸商信息
     */
    private StevedoringMerchant stevedoringMerchant;

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

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public ValidateIgnore getValidateIgnore() {
        return validateIgnore;
    }

    public void setValidateIgnore(ValidateIgnore validateIgnore) {
        this.validateIgnore = validateIgnore;
    }

    public String getSendVehicleDetailBizId() {
        return sendVehicleDetailBizId;
    }

    public void setSendVehicleDetailBizId(String sendVehicleDetailBizId) {
        this.sendVehicleDetailBizId = sendVehicleDetailBizId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Long getPreNextSiteCode() {
        return preNextSiteCode;
    }

    public void setPreNextSiteCode(Long preNextSiteCode) {
        this.preNextSiteCode = preNextSiteCode;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public StevedoringMerchant getStevedoringMerchant() {
        return stevedoringMerchant;
    }

    public void setStevedoringMerchant(StevedoringMerchant stevedoringMerchant) {
        this.stevedoringMerchant = stevedoringMerchant;
    }
}
