package com.jd.bluedragon.distribution.jy.dto.pickinggood;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class AirRailTaskCountQueryDto implements Serializable {
    private static final long serialVersionUID = 4869220727220937779L;

    private long pickingSiteId;

    private Integer taskType;

    private Date createTime;

    private List<String> bizIdList;

    public long getPickingSiteId() {
        return pickingSiteId;
    }

    public void setPickingSiteId(long pickingSiteId) {
        this.pickingSiteId = pickingSiteId;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<String> getBizIdList() {
        return bizIdList;
    }

    public void setBizIdList(List<String> bizIdList) {
        this.bizIdList = bizIdList;
    }
}
