package com.jd.bluedragon.common.dto.operation.workbench.unload.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @ClassName UnloadScanRequest
 * @Description 卸车完成
 * @Author wyh
 * @Date 2022/3/31 22:13
 **/
public class UnloadCompleteRequest implements Serializable {

    private static final long serialVersionUID = 4648667337916471503L;

    private User user;

    private CurrentOperate currentOperate;

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
    private Long shouldScanCount;

    /**
     * 多扫数量
     * 异常情况下必传
     */
    private Long moreScanCount;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

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
}
