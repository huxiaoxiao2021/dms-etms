package com.jd.bluedragon.distribution.inspection.domain;

import com.jd.bluedragon.distribution.api.domain.OperatorData;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName InspectionPackageMQ
 * @Description 包裹验货消息
 * @Author wyh
 * @Date 2020/11/12 15:07
 **/
public class InspectionPackageMQ implements Serializable {

    private static final long serialVersionUID = 5868424233298803588L;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 箱号
     */
    private String boxCode;

    /**
     * 操作人ID
     */
    private Integer operateUserId;

    /**
     * 操作人
     */
    private String operateUser;

    /**
     * 验货时间
     */
    private Date inspectionTime;

    /**
     * 操作分拣中心
     */
    private Integer operateSiteCode;

    /**
     * 验货类型
     */
    private Integer inspectionType;

    /**
     * 操作类型
     */
    private Integer operateType;

    /**
     * 异常类型
     */
    private String exceptionType;

    /**
     * 收货分拣中心
     */
    private Integer receiveSiteCode;

    private Date recordCreateTime;

    private String machineCode;//设备编码

    private Integer bizSource;
    /**
     * 按单验货标识
     */
    private Boolean waybillInspectionFlag;

    /**
     *@see com.jd.bluedragon.distribution.api.enums.OperatorTypeEnum
     * 操作者类型编码
     */
    private Integer operatorTypeCode;
    /**
     * 操作者id
     */
    private String operatorId;
    /**
     * 操作信息对象
     */
    private OperatorData operatorData;


    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getOperateUserId() {
        return operateUserId;
    }

    public void setOperateUserId(Integer operateUserId) {
        this.operateUserId = operateUserId;
    }

    public String getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(String operateUser) {
        this.operateUser = operateUser;
    }

    public Date getInspectionTime() {
        return inspectionTime;
    }

    public void setInspectionTime(Date inspectionTime) {
        this.inspectionTime = inspectionTime;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public Integer getInspectionType() {
        return inspectionType;
    }

    public void setInspectionType(Integer inspectionType) {
        this.inspectionType = inspectionType;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public Date getRecordCreateTime() {
        return recordCreateTime;
    }

    public void setRecordCreateTime(Date recordCreateTime) {
        this.recordCreateTime = recordCreateTime;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public Integer getBizSource() {
        return bizSource;
    }

    public void setBizSource(Integer bizSource) {
        this.bizSource = bizSource;
    }

    public Boolean getWaybillInspectionFlag() {
        return waybillInspectionFlag;
    }

    public void setWaybillInspectionFlag(Boolean waybillInspectionFlag) {
        this.waybillInspectionFlag = waybillInspectionFlag;
    }

    public Integer getOperatorTypeCode() {
        return operatorTypeCode;
    }

    public void setOperatorTypeCode(Integer operatorTypeCode) {
        this.operatorTypeCode = operatorTypeCode;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public OperatorData getOperatorData() {
        return operatorData;
    }

    public void setOperatorData(OperatorData operatorData) {
        this.operatorData = operatorData;
    }
}
