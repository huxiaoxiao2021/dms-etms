package com.jd.bluedragon.distribution.jy.dto.unload;



import java.io.Serializable;

/**
 * 卸车完成
 **/
public class UnloadCompleteDto extends UnloadBaseDto implements Serializable {

    private static final long serialVersionUID = 4648667337916471503L;

    /**
     * 任务主键
     */
    private String taskId;

    /**
     * 业务主键
     */
    private String bizId;

    /**
     * 封车编码
     */
    private String sealCarCode;

    /**
     * 是否异常；0-否 1-是
     */
    private Byte abnormalFlag;

    /**
     * 待扫描数量
     * 异常情况下必传
     */
    private Long toScanCount;

    /**
     * 本场地多扫数量
     */
    private Long moreScanLocalCount;

    /**
     * 非本场地多扫数量
     */
    private Long moreScanOutCount;


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public Byte getAbnormalFlag() {
        return abnormalFlag;
    }

    public void setAbnormalFlag(Byte abnormalFlag) {
        this.abnormalFlag = abnormalFlag;
    }

    public Long getToScanCount() {
        return toScanCount;
    }

    public void setToScanCount(Long toScanCount) {
        this.toScanCount = toScanCount;
    }

    public Long getMoreScanLocalCount() {
        return moreScanLocalCount;
    }

    public void setMoreScanLocalCount(Long moreScanLocalCount) {
        this.moreScanLocalCount = moreScanLocalCount;
    }

    public Long getMoreScanOutCount() {
        return moreScanOutCount;
    }

    public void setMoreScanOutCount(Long moreScanOutCount) {
        this.moreScanOutCount = moreScanOutCount;
    }
}
