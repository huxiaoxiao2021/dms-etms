package com.jd.bluedragon.distribution.sendGroup.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by jinjingcheng on 2017/6/28.
 */
public class SortMachineGroupConfig implements Serializable {
    private Long id;
    /*分组编号*/
    private Long groupId;
    /*滑槽号*/
    private String chuteCode;
    /*是否删除,0-删除,1-使用*/
    private Integer yn;
    private Date ts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getChuteCode() {
        return chuteCode;
    }

    public void setChuteCode(String chuteCode) {
        this.chuteCode = chuteCode;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }
}
