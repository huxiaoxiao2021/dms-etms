package com.jd.bluedragon.common.dto.collectpackage.request;

import java.io.Serializable;

public class SearchPackageTaskReq implements Serializable {
    private static final long serialVersionUID = -2582599082183192975L;

    /**
     * 任务状态
     */
    private Integer taskStatus;

    /**
     * 分页参数
     */
    private Integer pageNo;
    private Integer pageSize;

    /**
     * 检索条件，箱号|包裹号
     */
    private String barCode;

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

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
}
