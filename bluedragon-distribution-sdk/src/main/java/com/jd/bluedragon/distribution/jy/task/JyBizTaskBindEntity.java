package com.jd.bluedragon.distribution.jy.task;

import java.util.Date;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 20:04
 * @Description
 */
public class JyBizTaskBindEntity {
    private Long id;

    private String bizId;

    private String bindBizId;

    private String bindDetailBizId;

    private Integer operateSiteCode;

    /**
     * 绑定任务业务场景分类
     * com.jd.bluedragon.distribution.jy.constants.TaskBindTypeEnum
     */
    private Integer type;

    private String createUserErp;

    private String createUserName;

    private String updateUserErp;

    private String updateUserName;

    private Date createTime;

    private Date updateTime;

    private Integer yn;

    private Date ts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getBindBizId() {
        return bindBizId;
    }

    public void setBindBizId(String bindBizId) {
        this.bindBizId = bindBizId;
    }

    public String getBindDetailBizId() {
        return bindDetailBizId;
    }

    public void setBindDetailBizId(String bindDetailBizId) {
        this.bindDetailBizId = bindDetailBizId;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
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

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Integer getYn() {
        return yn;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }
}
