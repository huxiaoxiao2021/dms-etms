package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : caozhixing3
 * @version V1.0
 * @Project: ql-dms-distribution
 * @Package com.jd.bluedragon.distribution.jy.dto.send
 * @Description:
 * @date Date : 2024年01月24日 21:17
 */
public class SendScanCallbackReqDto implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     *操作单位编号
     */
    private int siteCode;
    /**
     *操作单位名称
     */
    private String siteName;
    /**
     * 操作人编号
     */
    private int userCode;
    /**
     * 操作人姓名
     */
    private String userName;
    /**
     * erp
     */
    private String userErp;
    /**
     *操作时间
     */
    private Date operateTime;
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
    /**
     * 单号
     */
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
     *
     */
    private String taskId;

    /**
     * 跳过发货拦截强制提交
     */
    private Boolean forceSubmit;

    /**
     * 装卸商编码
     */
    private String merchantCode;
    /**
     * 装卸商名称
     */
    private Long merchantName;

    //==================================

    public int getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(int siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public int getUserCode() {
        return userCode;
    }

    public void setUserCode(int userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }

    public String getSendVehicleDetailBizId() {
        return sendVehicleDetailBizId;
    }

    public void setSendVehicleDetailBizId(String sendVehicleDetailBizId) {
        this.sendVehicleDetailBizId = sendVehicleDetailBizId;
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

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Boolean getForceSubmit() {
        return forceSubmit;
    }

    public void setForceSubmit(Boolean forceSubmit) {
        this.forceSubmit = forceSubmit;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public Long getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(Long merchantName) {
        this.merchantName = merchantName;
    }
}
