package com.jd.bluedragon.distribution.jy.collectpackage;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author liwenji
 * @description 集包任务查询
 * @date 2023-10-17 18:19
 */
public class JyBizTaskCollectPackageQuery implements Serializable {

    /**
     * 分页参数-开始值
     */
    private int offset;
    /**
     * 分页参数-数据条数
     */
    private int limit;

    /**
     * 始发站点
     */
    private Long startSiteId;
    private Long endSiteId;

    /**
     * 任务状态
     */
    private Integer taskStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 箱号
     */
    private String boxCode;

    private List<String> bizIds;

    private List<Long> ids;

    private List<Integer> taskStatusList;

    private String updateUserErp;

    private String updateUserName;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Long getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Long startSiteId) {
        this.startSiteId = startSiteId;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public List<String> getBizIds() {
        return bizIds;
    }

    public void setBizIds(List<String> bizIds) {
        this.bizIds = bizIds;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public List<Integer> getTaskStatusList() {
        return taskStatusList;
    }

    public void setTaskStatusList(List<Integer> taskStatusList) {
        this.taskStatusList = taskStatusList;
    }

    public String getUpdateUserErp() {
        return updateUserErp;
    }

    public void setUpdateUserErp(String updateUserErp) {
        this.updateUserErp = updateUserErp;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }
}
