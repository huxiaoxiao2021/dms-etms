package com.jd.bluedragon.distribution.material.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName RecycleMaterialScanVO
 * @Description
 * @Author wyh
 * @Date 2020/2/27 17:49
 **/
public class RecycleMaterialScanVO implements Serializable {

    private static final long serialVersionUID = -2885432827565053688L;

    /**
     * 物资类型 1：保温箱；99：其它
     */
    private Byte materialType;

    /**
     * 物资编号
     */
    private String materialCode;

    /**
     * 板号
     */
    private String boardCode;

    /**
     * 扫描类型；1：入库 2：出库
     */
    private Byte scanType;

    /**
     * 分拣中心ID
     */
    private Long createSiteCode;

    /**
     * 操作机构
     */
    private String createSiteName;

    /**
     * erp账号
     */
    private String userErp;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 物资状态；1：已入库未出库 2：已出库
     */
    private Byte materialStatus;

    public Byte getMaterialType() {
        return materialType;
    }

    public void setMaterialType(Byte materialType) {
        this.materialType = materialType;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public Byte getScanType() {
        return scanType;
    }

    public void setScanType(Byte scanType) {
        this.scanType = scanType;
    }

    public Long getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Long createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
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

    public Byte getMaterialStatus() {
        return materialStatus;
    }

    public void setMaterialStatus(Byte materialStatus) {
        this.materialStatus = materialStatus;
    }
}
