package com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * InspectionCommonRequest
 * @author fanggang7
 * @time 2022-10-09 14:43:56 周日
 */
public class InspectionCommonRequest extends BaseReq implements Serializable {

    private static final long serialVersionUID = -8613387947485016760L;

    /**
     * 任务主键
     */
    private String taskId;

    /**
     * 业务主键
     */
    private String bizId;

    private Integer pageNumber;

    private Integer pageSize;

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

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
