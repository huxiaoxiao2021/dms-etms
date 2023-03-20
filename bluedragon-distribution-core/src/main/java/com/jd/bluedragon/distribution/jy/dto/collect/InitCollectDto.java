package com.jd.bluedragon.distribution.jy.dto.collect;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/
public class InitCollectDto implements Serializable {

    private static final long serialVersionUID = -6963372061306635997L;

    /**
     * 封车编码
     */
    private String bizId;
    /**
     * 操作时间
     */
    private Long operateTime;
    /**
     * 初始化时机： InitCollectNodeEnum
     */
    private Integer operateNode;

    /**
     * 空任务扫描面单类型 （空任务按运单扫描时，除了初始化数据，还需要完成整个运单集齐状态的变更）
     * ScanCodeTypeEnum
     */
    private Integer taskNullScanCodeType;
    /**
     * 空任务扫描面单号
     */
    private String taskNullScanCode;
    /**
     * 空任务扫描场地编码
     */
    private Integer taskNullScanSiteCode;

    private String operatorErp;

    /**
     * 集齐场地
     */
    private Integer collectNodeSiteCode;

    private String waybillCode;
    private String sealBatchCode;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getOperateNode() {
        return operateNode;
    }

    public void setOperateNode(Integer operateNode) {
        this.operateNode = operateNode;
    }

    public Integer getTaskNullScanCodeType() {
        return taskNullScanCodeType;
    }

    public void setTaskNullScanCodeType(Integer taskNullScanCodeType) {
        this.taskNullScanCodeType = taskNullScanCodeType;
    }

    public String getTaskNullScanCode() {
        return taskNullScanCode;
    }

    public void setTaskNullScanCode(String taskNullScanCode) {
        this.taskNullScanCode = taskNullScanCode;
    }

    public Integer getTaskNullScanSiteCode() {
        return taskNullScanSiteCode;
    }

    public void setTaskNullScanSiteCode(Integer taskNullScanSiteCode) {
        this.taskNullScanSiteCode = taskNullScanSiteCode;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
    }

    public Integer getCollectNodeSiteCode() {
        return collectNodeSiteCode;
    }

    public void setCollectNodeSiteCode(Integer collectNodeSiteCode) {
        this.collectNodeSiteCode = collectNodeSiteCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getSealBatchCode() {
        return sealBatchCode;
    }

    public void setSealBatchCode(String sealBatchCode) {
        this.sealBatchCode = sealBatchCode;
    }
}

