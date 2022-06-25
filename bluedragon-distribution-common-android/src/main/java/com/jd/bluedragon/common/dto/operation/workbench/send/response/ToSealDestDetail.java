package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName ToSealDestDetail
 * @Description
 * @Author wyh
 * @Date 2022/5/19 18:23
 **/
public class ToSealDestDetail implements Serializable {

    private static final long serialVersionUID = -3526443184706137200L;

    /**
     * 发货流向任务明细
     */
    private String sendDetailBizId;

    /**
     * 发货目的地
     */
    private Integer endSiteId;

    /**
     * 发货目的地
     */
    private String endSiteName;

    /**
     * 待扫包裹数
     */
    private Long toScanPackCount = 0L;

    /**
     * 已扫包裹数
     */
    private Long scannedPackCount = 0L;

    /**
     * 预计发货时间
     */
    private Date planDepartTime;

    /**
     * 状态
     */
    private Integer itemStatus;

    /**
     * 状态描述
     */
    private String itemStatusDesc;

    public Integer getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Integer endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public Long getToScanPackCount() {
        return toScanPackCount;
    }

    public void setToScanPackCount(Long toScanPackCount) {
        this.toScanPackCount = toScanPackCount;
    }

    public Long getScannedPackCount() {
        return scannedPackCount;
    }

    public void setScannedPackCount(Long scannedPackCount) {
        this.scannedPackCount = scannedPackCount;
    }

    public Date getPlanDepartTime() {
        return planDepartTime;
    }

    public void setPlanDepartTime(Date planDepartTime) {
        this.planDepartTime = planDepartTime;
    }

    public Integer getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(Integer itemStatus) {
        this.itemStatus = itemStatus;
    }

    public String getItemStatusDesc() {
        return itemStatusDesc;
    }

    public void setItemStatusDesc(String itemStatusDesc) {
        this.itemStatusDesc = itemStatusDesc;
    }

    public String getSendDetailBizId() {
        return sendDetailBizId;
    }

    public void setSendDetailBizId(String sendDetailBizId) {
        this.sendDetailBizId = sendDetailBizId;
    }
}
