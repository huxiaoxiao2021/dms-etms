package com.jd.bluedragon.distribution.spotcheck.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 抽检申诉记录定时任务对象
 */
public class SpotCheckAppealTaskDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
