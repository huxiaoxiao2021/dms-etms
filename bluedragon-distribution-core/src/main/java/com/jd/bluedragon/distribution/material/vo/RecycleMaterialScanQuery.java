package com.jd.bluedragon.distribution.material.vo;

import com.jd.bluedragon.Pager;
import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName RecycleMaterialScanQuery
 * @Description
 * @Author wyh
 * @Date 2020/2/27 17:30
 **/
public class RecycleMaterialScanQuery extends BasePagerCondition {

    private static final long serialVersionUID = -1122709575985897801L;

    /**
     * 当前分拣中心
     */
    private Long createSiteCode;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 物资状态；1：已入库未出库 2：已出库
     */
    private Byte materialStatus;

    /**
     * 扫描类型；1：入库 2：出库
     */
    private Byte scanType;

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
     * erp账号
     */
    private String userErp;

    private Pager<List<RecycleMaterialScanVO>> pager;

    public Long getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Long createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Byte getMaterialStatus() {
        return materialStatus;
    }

    public void setMaterialStatus(Byte materialStatus) {
        this.materialStatus = materialStatus;
    }

    public Byte getScanType() {
        return scanType;
    }

    public void setScanType(Byte scanType) {
        this.scanType = scanType;
    }

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

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public Pager<List<RecycleMaterialScanVO>> getPager() {
        return pager;
    }

    public void setPager(Pager<List<RecycleMaterialScanVO>> pager) {
        this.pager = pager;
    }
}
