package com.jd.bluedragon.common.dto.working;

import java.io.Serializable;

public class WorkingClockRequest implements Serializable {
    private static final long serialVersionUID = 4113753006005526448L;
    /**
     * 打卡人的erp
     */
    private String employeeErp;

    /**
     * 操作人的erp
     */
    private String operatorErp;

    /**
     * 日期
     */
    private String workingDate;

    /**
     * true:签到, false:签退
     */
    private Boolean sinIn;

    /**
     * 要操作的打卡记录id
     */
    private Long recordId;

    /**
     * 调整后的 签到/签退 时间
     */
    private String newClockTime;

    /**
     * 确认
     */
    private Boolean confirm;


    private Integer offset;

    private Integer pageNo;

    private Integer size;

    public String getEmployeeErp() {
        return employeeErp;
    }

    public void setEmployeeErp(String employeeErp) {
        this.employeeErp = employeeErp;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
    }

    public String getWorkingDate() {
        return workingDate;
    }

    public void setWorkingDate(String workingDate) {
        this.workingDate = workingDate;
    }

    public Boolean getSinIn() {
        return sinIn;
    }

    public void setSinIn(Boolean sinIn) {
        this.sinIn = sinIn;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getNewClockTime() {
        return newClockTime;
    }

    public void setNewClockTime(String newClockTime) {
        this.newClockTime = newClockTime;
    }

    public Boolean getConfirm() {
        return confirm;
    }

    public void setConfirm(Boolean confirm) {
        this.confirm = confirm;
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

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
