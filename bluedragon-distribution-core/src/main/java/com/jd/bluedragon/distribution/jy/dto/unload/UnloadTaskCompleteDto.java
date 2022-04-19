package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName UnloadTaskCompleteDto
 * @Description
 * @Author wyh
 * @Date 2022/4/9 20:16
 **/
public class UnloadTaskCompleteDto implements Serializable {

    private static final long serialVersionUID = 4855347699013606767L;

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
     */
    private Long shouldScanCount;

    /**
     * 多扫数量
     */
    private Long moreScanCount;

    /**
     * 操作时间
     */
    private Date operateTime;

    private String operateUserErp;

    private String operateUserName;

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

    public Long getShouldScanCount() {
        return shouldScanCount;
    }

    public void setShouldScanCount(Long shouldScanCount) {
        this.shouldScanCount = shouldScanCount;
    }

    public Long getMoreScanCount() {
        return moreScanCount;
    }

    public void setMoreScanCount(Long moreScanCount) {
        this.moreScanCount = moreScanCount;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }
}
