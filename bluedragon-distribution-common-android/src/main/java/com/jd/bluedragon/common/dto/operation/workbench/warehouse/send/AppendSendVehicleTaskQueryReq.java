package com.jd.bluedragon.common.dto.operation.workbench.warehouse.send;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.Date;

/**
 * 追加混扫任务列表查询
 */
public class AppendSendVehicleTaskQueryReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = -5809332610524693231L;

    /**
     * 搜索关键字
     */
    private String keyword;
    /**
     * 混扫任务编码
     */
    private String mixScanTaskCode;

    private Integer pageSize;
    private Integer pageNo;


    /**
     * 最晚计划发车时间 范围查找-开始时间
     */
    private Date lastPlanDepartTimeBegin;

    /**
     * 最晚计划发车时间 范围查找-结束时间
     */
    private Date lastPlanDepartTimeEnd;


    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public String getMixScanTaskCode() {
        return mixScanTaskCode;
    }

    public void setMixScanTaskCode(String mixScanTaskCode) {
        this.mixScanTaskCode = mixScanTaskCode;
    }

    public Date getLastPlanDepartTimeBegin() {
        return lastPlanDepartTimeBegin;
    }

    public void setLastPlanDepartTimeBegin(Date lastPlanDepartTimeBegin) {
        this.lastPlanDepartTimeBegin = lastPlanDepartTimeBegin;
    }

    public Date getLastPlanDepartTimeEnd() {
        return lastPlanDepartTimeEnd;
    }

    public void setLastPlanDepartTimeEnd(Date lastPlanDepartTimeEnd) {
        this.lastPlanDepartTimeEnd = lastPlanDepartTimeEnd;
    }
}
