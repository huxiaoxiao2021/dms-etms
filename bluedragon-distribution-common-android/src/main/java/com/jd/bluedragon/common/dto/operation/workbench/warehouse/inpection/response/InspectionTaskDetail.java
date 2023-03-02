package com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.response;

import java.io.Serializable;

/**
 * 接货仓验货任务明细
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-10-09 14:19:52 周日
 */
public class InspectionTaskDetail implements Serializable {

    private static final long serialVersionUID = -7823254733387066564L;

    /**
     * 任务主键
     */
    private String taskId;

    /**
     * 验货任务业务主键
     */
    private String bizId;

    /**
     * 已卸包裹总数
     */
    private Long scanCount;

    /**
     * 拦截已扫数量
     */
    private Long interceptScanCount;

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

    public Long getScanCount() {
        return scanCount;
    }

    public void setScanCount(Long scanCount) {
        this.scanCount = scanCount;
    }

    public Long getInterceptScanCount() {
        return interceptScanCount;
    }

    public void setInterceptScanCount(Long interceptScanCount) {
        this.interceptScanCount = interceptScanCount;
    }
}
