package com.jd.bluedragon.distribution.print.request;

import java.io.Serializable;

/**
 * @author wyh
 * @className PrintCompleteRequest
 * @description
 * @date 2021/11/24 14:58
 **/
public class SiteTerminalPrintCompleteRequest implements Serializable {

    private static final long serialVersionUID = -4428873050145686682L;

    /**
     * 运单号或包裹号，根据单号类型判断
     */
    private String barCode;

    /**
     * 运单waybillSign
     */
    private String waybillSign;

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

    /**
     * 是否首次打印。使用方限制首次非首次 1：是 0：否
     */
    private Integer firstTimePrint;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getWaybillSign() {
        return waybillSign;
    }

    public void setWaybillSign(String waybillSign) {
        this.waybillSign = waybillSign;
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

    public Integer getFirstTimePrint() {
        return firstTimePrint;
    }

    public void setFirstTimePrint(Integer firstTimePrint) {
        this.firstTimePrint = firstTimePrint;
    }
}
