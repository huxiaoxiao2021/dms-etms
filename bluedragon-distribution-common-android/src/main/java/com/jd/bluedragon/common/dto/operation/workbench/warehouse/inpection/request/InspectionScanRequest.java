package com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * 验货扫描接口
 *
 * @author fanggang7
 * @time 2022-10-09 14:31:03 周日
 */
public class InspectionScanRequest extends BaseReq implements Serializable {

    private static final long serialVersionUID = 4648667337916471503L;

    /**
     * 单号
     */
    private String barCode;

    /**
     * 扫描方式
     */
    private Integer scanType;

    /**
     * 任务主键
     */
    private String taskId;

    /**
     * 业务主键
     */
    private String bizId;

    /**
     * 跳过拦截强制提交
     */
    private Boolean forceSubmit;

    /**
     * 任务组号
     */
    private String groupCode;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getScanType() {
        return scanType;
    }

    public void setScanType(Integer scanType) {
        this.scanType = scanType;
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

    public Boolean getForceSubmit() {
        return forceSubmit;
    }

    public void setForceSubmit(Boolean forceSubmit) {
        this.forceSubmit = forceSubmit;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }
}
