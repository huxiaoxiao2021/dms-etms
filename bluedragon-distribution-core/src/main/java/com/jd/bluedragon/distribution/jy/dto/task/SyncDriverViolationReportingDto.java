package com.jd.bluedragon.distribution.jy.dto.task;

import java.io.Serializable;
import java.util.List;

/**
 * @author pengchong28
 * @description 发车任务封车上报司机违规数据
 * @date 2024/4/23
 */
public class SyncDriverViolationReportingDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 封车编码
     */
    private String sealCarCode;
    /**
     * 10 封车，20解封车，30进围栏，40出围栏
     */
    private Integer status;
    /**
     * 操作人ERP
     */
    private String operateUserCode;
    /**
     * 操作人名字
     */
    private String operateUserName;
    /**
     * 操作时间
     */
    private String operateTime;
    /**
     * 批次 JSON格式
     */
    private List<String> batchCodes;
    /**
     * 运输封车批次集合拆分，下游按单批次处理
     * batchCodes 中的某一个
     */
    private String singleBatchCode;
    /**
     * 派车明细单号
     */
    private String transWorkItemCode;

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOperateUserCode() {
        return operateUserCode;
    }

    public void setOperateUserCode(String operateUserCode) {
        this.operateUserCode = operateUserCode;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public List<String> getBatchCodes() {
        return batchCodes;
    }

    public void setBatchCodes(List<String> batchCodes) {
        this.batchCodes = batchCodes;
    }

    public String getSingleBatchCode() {
        return singleBatchCode;
    }

    public void setSingleBatchCode(String singleBatchCode) {
        this.singleBatchCode = singleBatchCode;
    }

    public String getTransWorkItemCode() {
        return transWorkItemCode;
    }

    public void setTransWorkItemCode(String transWorkItemCode) {
        this.transWorkItemCode = transWorkItemCode;
    }
}
