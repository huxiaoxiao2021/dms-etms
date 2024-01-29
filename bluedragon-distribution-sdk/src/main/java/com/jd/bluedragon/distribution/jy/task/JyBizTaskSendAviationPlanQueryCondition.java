package com.jd.bluedragon.distribution.jy.task;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 15:06
 * @Description
 */
public class JyBizTaskSendAviationPlanQueryCondition extends JyBizTaskSendAviationPlanEntity implements Serializable {

    private static final long serialVersionUID = 4089383783438643445L;

    private List<Integer> nextSiteIdList;

    private List<Integer> taskStatusList;

    private List<String> bizIdList;

    private Integer pageSize;

    private Integer offset;
    //起止创建时间
    private Date createTimeStart;
    private Date createTimeEnd;

    //起止起飞时间
    private Date takeOffTimeStart;
    private Date takeOffTimeEnd;
    /**
     * 按起飞时间倒排序
     * 1 逆序，否则不做逆序
     */
    private Integer takeOffTimeOrderDesc;

    public List<Integer> getNextSiteIdList() {
        return nextSiteIdList;
    }

    public void setNextSiteIdList(List<Integer> nextSiteIdList) {
        this.nextSiteIdList = nextSiteIdList;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public List<Integer> getTaskStatusList() {
        return taskStatusList;
    }

    public void setTaskStatusList(List<Integer> taskStatusList) {
        this.taskStatusList = taskStatusList;
    }

    public List<String> getBizIdList() {
        return bizIdList;
    }

    public void setBizIdList(List<String> bizIdList) {
        this.bizIdList = bizIdList;
    }

    public Date getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeStart(Date createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public Date getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(Date createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public Date getTakeOffTimeStart() {
        return takeOffTimeStart;
    }

    public void setTakeOffTimeStart(Date takeOffTimeStart) {
        this.takeOffTimeStart = takeOffTimeStart;
    }

    public Date getTakeOffTimeEnd() {
        return takeOffTimeEnd;
    }

    public void setTakeOffTimeEnd(Date takeOffTimeEnd) {
        this.takeOffTimeEnd = takeOffTimeEnd;
    }

    public Integer getTakeOffTimeOrderDesc() {
        return takeOffTimeOrderDesc;
    }

    public void setTakeOffTimeOrderDesc(Integer takeOffTimeOrderDesc) {
        this.takeOffTimeOrderDesc = takeOffTimeOrderDesc;
    }
}
