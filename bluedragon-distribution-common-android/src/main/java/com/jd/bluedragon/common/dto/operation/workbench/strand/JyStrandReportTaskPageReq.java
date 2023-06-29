package com.jd.bluedragon.common.dto.operation.workbench.strand;

import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizStrandTaskStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizStrandTaskTypeEnum;

import java.io.Serializable;

/**
 * 拣运app-滞留上报任务分页列表请求体
 *
 * @author hujiping
 * @date 2023/3/27 4:33 PM
 */
public class JyStrandReportTaskPageReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 场地编码
     */
    private Integer siteCode;

    /**
     * 岗位码
     */
    private String positionCode;

    /**
     * 任务类型
     * @see JyBizStrandTaskTypeEnum
     */
    private Integer taskType;

    /**
     * 任务状态
     * @see JyBizStrandTaskStatusEnum
     */
    private Integer taskStatus;
    
    private Integer offset;

    private Integer pageNo;

    private Integer pageSize;

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
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
}
