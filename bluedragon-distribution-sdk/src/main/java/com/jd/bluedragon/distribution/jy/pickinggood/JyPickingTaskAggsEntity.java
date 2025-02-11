package com.jd.bluedragon.distribution.jy.pickinggood;

import java.io.Serializable;
import java.util.Date;

/**
 * 空提航班任务维度统计表
 * 上游发ABC, 提货CD：  待提2-A、B，交接提1-C，多提1-D
 *
 * 多提+发货=多发
 */
public class JyPickingTaskAggsEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;

    private String bizId;
    /**
     * 提货场地id
     */
    private Long pickingSiteId;
    /**
     * 提货机场编码/提货车站编码
     */
    private String pickingNodeCode;

    private Integer receiveItemCount;
    /**
     * 待提包裹件数
     */
    private Integer waitScanPackageCount;
    /**
     * 待提箱件数
     */
    private Integer waitScanBoxCount;
    /**
     * 待提总件数
     */
    private Integer waitScanTotalCount;
    /**
     * 交接已提包裹总数
     */
    private Integer handoverScanPackageCount;
    /**
     * 交接已提箱总数
     */
    private Integer handoverScanBoxCount;
    /**
     * 交接已提总件数
     */
    private Integer handoverScanTotalCount;
    /**
     * 已提包裹总件数[已提=交接提+多提]
     */
    private Integer scanPackageTotalCount;
    /**
     * 已提箱总件数[已提=交接提+多提]
     */
    private Integer scanBoxTotalCount;
    /**
     * 已提总件数[已提=交接提+多提]
     */
    private Integer scanTotalCount;
    /**
     * 多提包裹件数
     */
    private Integer moreScanPackageCount;
    /**
     * 多提箱件数
     */
    private Integer moreScanBoxCount;
    /**
     * 多提总件数
     */
    private Integer moreScanTotalCount;
    /**
     * 已发包裹件数【含多发】
     */
    private Integer sendPackageCount;
    /**
     * 已发箱件数【含多发】
     */
    private Integer sendBoxCount;
    /**
     * 已发总件数【含多发】
     */
    private Integer sendTotalCount;
    /**
     * 多发包裹件数
     */
    private Integer moreSendPackageCount;
    /**
     * 多发箱件数
     */
    private Integer moreSendBoxCount;
    /**
     * 多发总件数
     */
    private Integer moreSendTotalCount;

    private Date createTime;

    private Date updateTime;

    private Integer yn;

    private Date ts;

    public JyPickingTaskAggsEntity() {
    }

    public JyPickingTaskAggsEntity(Long pickingSiteId, String bizId) {
        this.pickingSiteId = pickingSiteId;
        this.bizId = bizId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Long getPickingSiteId() {
        return pickingSiteId;
    }

    public void setPickingSiteId(Long pickingSiteId) {
        this.pickingSiteId = pickingSiteId;
    }

    public String getPickingNodeCode() {
        return pickingNodeCode;
    }

    public void setPickingNodeCode(String pickingNodeCode) {
        this.pickingNodeCode = pickingNodeCode;
    }

    public Integer getReceiveItemCount() {
        return receiveItemCount;
    }

    public void setReceiveItemCount(Integer receiveItemCount) {
        this.receiveItemCount = receiveItemCount;
    }

    public Integer getWaitScanPackageCount() {
        return waitScanPackageCount;
    }

    public void setWaitScanPackageCount(Integer waitScanPackageCount) {
        this.waitScanPackageCount = waitScanPackageCount;
    }

    public Integer getWaitScanBoxCount() {
        return waitScanBoxCount;
    }

    public void setWaitScanBoxCount(Integer waitScanBoxCount) {
        this.waitScanBoxCount = waitScanBoxCount;
    }

    public Integer getWaitScanTotalCount() {
        return waitScanTotalCount;
    }

    public void setWaitScanTotalCount(Integer waitScanTotalCount) {
        this.waitScanTotalCount = waitScanTotalCount;
    }

    public Integer getHandoverScanPackageCount() {
        return handoverScanPackageCount;
    }

    public void setHandoverScanPackageCount(Integer handoverScanPackageCount) {
        this.handoverScanPackageCount = handoverScanPackageCount;
    }

    public Integer getHandoverScanBoxCount() {
        return handoverScanBoxCount;
    }

    public void setHandoverScanBoxCount(Integer handoverScanBoxCount) {
        this.handoverScanBoxCount = handoverScanBoxCount;
    }

    public Integer getHandoverScanTotalCount() {
        return handoverScanTotalCount;
    }

    public void setHandoverScanTotalCount(Integer handoverScanTotalCount) {
        this.handoverScanTotalCount = handoverScanTotalCount;
    }

    public Integer getScanPackageTotalCount() {
        return scanPackageTotalCount;
    }

    public void setScanPackageTotalCount(Integer scanPackageTotalCount) {
        this.scanPackageTotalCount = scanPackageTotalCount;
    }

    public Integer getScanBoxTotalCount() {
        return scanBoxTotalCount;
    }

    public void setScanBoxTotalCount(Integer scanBoxTotalCount) {
        this.scanBoxTotalCount = scanBoxTotalCount;
    }

    public Integer getScanTotalCount() {
        return scanTotalCount;
    }

    public void setScanTotalCount(Integer scanTotalCount) {
        this.scanTotalCount = scanTotalCount;
    }

    public Integer getMoreScanPackageCount() {
        return moreScanPackageCount;
    }

    public void setMoreScanPackageCount(Integer moreScanPackageCount) {
        this.moreScanPackageCount = moreScanPackageCount;
    }

    public Integer getMoreScanBoxCount() {
        return moreScanBoxCount;
    }

    public void setMoreScanBoxCount(Integer moreScanBoxCount) {
        this.moreScanBoxCount = moreScanBoxCount;
    }

    public Integer getMoreScanTotalCount() {
        return moreScanTotalCount;
    }

    public void setMoreScanTotalCount(Integer moreScanTotalCount) {
        this.moreScanTotalCount = moreScanTotalCount;
    }

    public Integer getSendPackageCount() {
        return sendPackageCount;
    }

    public void setSendPackageCount(Integer sendPackageCount) {
        this.sendPackageCount = sendPackageCount;
    }

    public Integer getSendBoxCount() {
        return sendBoxCount;
    }

    public void setSendBoxCount(Integer sendBoxCount) {
        this.sendBoxCount = sendBoxCount;
    }

    public Integer getSendTotalCount() {
        return sendTotalCount;
    }

    public void setSendTotalCount(Integer sendTotalCount) {
        this.sendTotalCount = sendTotalCount;
    }

    public Integer getMoreSendPackageCount() {
        return moreSendPackageCount;
    }

    public void setMoreSendPackageCount(Integer moreSendPackageCount) {
        this.moreSendPackageCount = moreSendPackageCount;
    }

    public Integer getMoreSendBoxCount() {
        return moreSendBoxCount;
    }

    public void setMoreSendBoxCount(Integer moreSendBoxCount) {
        this.moreSendBoxCount = moreSendBoxCount;
    }

    public Integer getMoreSendTotalCount() {
        return moreSendTotalCount;
    }

    public void setMoreSendTotalCount(Integer moreSendTotalCount) {
        this.moreSendTotalCount = moreSendTotalCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }
}