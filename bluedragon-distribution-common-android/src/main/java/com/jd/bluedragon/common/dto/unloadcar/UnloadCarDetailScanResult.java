package com.jd.bluedragon.common.dto.unloadCar;

import java.io.Serializable;

/**
 * 卸车任务扫描明细结果
 *
 * @author: hujiping
 * @date: 2020/6/24 13:43
 */
public class UnloadCarDetailScanResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 封车编码
     * */
    private String sealCarCode;
    /**
     * 板号
     * */
    private String boardCode;
    /**
     * 运单号
     * */
    private String waybillCode;
    /**
     * 已扫包裹数
     * */
    private Integer packageScanCount;
    /**
     * 未扫包裹数
     * */
    private Integer packageUnScanCount;
    /**
     * 目的编码
     * */
    private Integer receiveSiteCode;
    /**
     * 目的名称
     * */
    private String receiveSiteName;
    /**
     * 操作站点
     * */
    private String operateSiteCode;
    /**
     * 操作站点名称
     * */
    private String operateSiteName;

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getPackageScanCount() {
        return packageScanCount;
    }

    public void setPackageScanCount(Integer packageScanCount) {
        this.packageScanCount = packageScanCount;
    }

    public Integer getPackageUnScanCount() {
        return packageUnScanCount;
    }

    public void setPackageUnScanCount(Integer packageUnScanCount) {
        this.packageUnScanCount = packageUnScanCount;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public String getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(String operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }
}
