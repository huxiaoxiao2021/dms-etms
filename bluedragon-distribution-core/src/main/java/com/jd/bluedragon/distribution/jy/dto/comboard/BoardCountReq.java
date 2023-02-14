package com.jd.bluedragon.distribution.jy.dto.comboard;

import com.jd.jddl.executor.function.scalar.filter.In;

import java.util.Date;
import java.util.List;

/**
 * @author liwenji
 * @date 2022-12-02 11:31
 */
public class BoardCountReq {

    private Long startSiteId;

    private List<Integer> endSiteIdList;
    
    private Date createTime;
    
    private List<Integer> statusList;

    private List<Integer> comboardSourceList;

    public Long getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Long startSiteId) {
        this.startSiteId = startSiteId;
    }

    public List<Integer> getEndSiteIdList() {
        return endSiteIdList;
    }

    public void setEndSiteIdList(List<Integer> endSiteIdList) {
        this.endSiteIdList = endSiteIdList;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<Integer> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Integer> statusList) {
        this.statusList = statusList;
    }

    public List<Integer> getComboardSourceList() {
        return comboardSourceList;
    }

    public void setComboardSourceList(List<Integer> comboardSourceList) {
        this.comboardSourceList = comboardSourceList;
    }
}
