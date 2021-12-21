package com.jd.bluedragon.distribution.popPrint.dto;

import java.io.Serializable;

/**
 * @author wyh
 * @className PushPrintRecordDto
 * @description
 * @date 2021/11/25 20:27
 **/
public class PushPrintRecordDto implements Serializable {

    private static final long serialVersionUID = -5381187890301620018L;

    public static final Integer FIRST_PRINT = 1;
    public static final Integer REPRINT = 2;

    /**
     * 业务类型
     */
    private Integer businessType;

    /**
     * 业务操作类型
     */
    private Integer operateType;

    /**
     * 1：首次打印 2：补打
     */
    private Integer printType;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 操作场地
     */
    private Integer siteCode;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 操作人ERP
     */
    private String operatorErp;

    /**
     * 操作时间
     */
    private Long opeTime;

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public Integer getPrintType() {
        return printType;
    }

    public void setPrintType(Integer printType) {
        this.printType = printType;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
    }

    public Long getOpeTime() {
        return opeTime;
    }

    public void setOpeTime(Long opeTime) {
        this.opeTime = opeTime;
    }
}
