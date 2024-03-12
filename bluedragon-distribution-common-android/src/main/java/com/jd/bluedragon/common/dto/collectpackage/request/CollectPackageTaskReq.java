package com.jd.bluedragon.common.dto.collectpackage.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.List;

public class CollectPackageTaskReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -7044667973061864111L;

    /**
     * 任务状态
     */
    private Integer taskStatus;

    /**
     * 分页参数
     */
    private Integer pageNo;
    private Integer pageSize;

    private List<String> supportBoxTypes;

    public List<String> getSupportBoxTypes() {
        return supportBoxTypes;
    }

    public void setSupportBoxTypes(List<String> supportBoxTypes) {
        this.supportBoxTypes = supportBoxTypes;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
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
