package com.jd.bluedragon.distribution.api.domain;

import java.io.Serializable;
import java.util.Date;

public class Rule implements Serializable{
    private static final long serialVersionUID = 1L;

    public static final String IN = "IN";

    public static final String OUT = "OUT";

    private Integer type;
    private String content;
    private String inOut;

    private long ruleId;//主键
    private Integer siteCode;//站点编码
    private String memo;//备注
    private Date createTime;//创建时间
    private Date updateTime;//更新时间
    private Long ts;//数据更新时间戳
    private Integer yn;//是否删除

    public long getRuleId() {
        return ruleId;
    }

    public void setRuleId(long ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getInOut() {
        return this.inOut;
    }

    public void setInOut(String inOut) {
        this.inOut = inOut;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }
}
