package com.jd.bluedragon.common.dto.unloadcar;

import java.io.Serializable;

/**
 * 卸车任务扫描结果
 *
 * @author: hujiping
 * @date: 2020/6/23 15:25
 */
public class UnloadCarScanResult implements Serializable {

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
     * 运单号/包裹号
     * */
    private String barCode;
    /**
     * 目的编码
     * */
    private Integer receiveSiteCode;
    /**
     * 目的名称
     * */
    private String receiveSiteName;
    /**
     * 包裹总数
     * */
    private Integer packageTotalCount;
    /**
     * 已扫包裹
     * */
    private Integer packageScanCount;
    /**
     * 未扫包裹
     * */
    private Integer packageUnScanCount;
    /**
     * 多数包裹
     * */
    private Integer surplusPackageScanCount;

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

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
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

    public Integer getPackageTotalCount() {
        return packageTotalCount;
    }

    public void setPackageTotalCount(Integer packageTotalCount) {
        this.packageTotalCount = packageTotalCount;
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

    public Integer getSurplusPackageScanCount() {
        return surplusPackageScanCount;
    }

    public void setSurplusPackageScanCount(Integer surplusPackageScanCount) {
        this.surplusPackageScanCount = surplusPackageScanCount;
    }
}
