package com.jd.bluedragon.distribution.jy.dto.task;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/11/6 16:01
 * @Description
 */
public class SealSyncOpenCloseSendTaskDto implements Serializable {

    private static final long serialVersionUID = -2720516986077944201L;
    public static final Integer STATUS_SEAL = 1;
    public static final Integer STATUS_UNSEAL = 2;


    /**
     * 封车编码
     */
    private String sealCarCode;
    /**
     * com.jd.bluedragon.distribution.jy.dto.task.SealUnsealStatusSyncAppSendTaskMQDto#STATUS_SEAL 封车同步
     * com.jd.bluedragon.distribution.jy.dto.task.SealUnsealStatusSyncAppSendTaskMQDto#STATUS_UNSEAL 取消封车同步
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
    private Long operateTime;
    /**
     * 批次 JSON格式
     */
    private List<String> batchCodes;
    /**
     * 运输封车批次集合拆分
     * batchCodes 中的某一个
     */
    private String singleBatchCode;

    private Long sysTime;

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

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
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

    public Long getSysTime() {
        return sysTime;
    }

    public void setSysTime(Long sysTime) {
        this.sysTime = sysTime;
    }

    public String getTransWorkItemCode() {
        return transWorkItemCode;
    }

    public void setTransWorkItemCode(String transWorkItemCode) {
        this.transWorkItemCode = transWorkItemCode;
    }
}
