package com.jd.bluedragon.distribution.jy.collectpackage;

import java.io.Serializable;
import java.util.Date;

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
}
