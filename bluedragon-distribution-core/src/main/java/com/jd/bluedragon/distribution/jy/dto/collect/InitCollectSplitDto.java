package com.jd.bluedragon.distribution.jy.dto.collect;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/
public class InitCollectSplitDto implements Serializable {

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
    private Integer pageNo;
    private Integer pageSize;


    //***************封车节点处理集齐拆分关注字段-*************************
    /**
     * 封车场地
     */
    private Integer sealSiteCode;
    /**
     * 应解封车场地：流向
     */
    private Integer shouldUnSealSiteCode;
    /**
     * 封车批次号
     */
    private String sealBatchCode;


    //***************空任务扫描节点处理集齐拆分关注字段*************************
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

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSealBatchCode() {
        return sealBatchCode;
    }

    public void setSealBatchCode(String sealBatchCode) {
        this.sealBatchCode = sealBatchCode;
    }

    public Integer getSealSiteCode() {
        return sealSiteCode;
    }

    public void setSealSiteCode(Integer sealSiteCode) {
        this.sealSiteCode = sealSiteCode;
    }

    public Integer getShouldUnSealSiteCode() {
        return shouldUnSealSiteCode;
    }

    public void setShouldUnSealSiteCode(Integer shouldUnSealSiteCode) {
        this.shouldUnSealSiteCode = shouldUnSealSiteCode;
    }
}

