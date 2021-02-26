package com.jd.bluedragon.distribution.rule.dto;

import java.io.Serializable;
import java.util.Date;

public class SortingRuleDto implements Serializable{

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

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 特殊字段 标识是否开启，方便使用者使用，无需关心 inOut content
     */
    private boolean openFlag;

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

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public boolean getOpenFlag() {
        return openFlag;
    }

    public void setOpenFlag(boolean openFlag) {
        this.openFlag = openFlag;
    }
}
