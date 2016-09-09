package com.jd.bluedragon.alpha.domain;

/**
 * Created by wuzuxiang on 2016/8/25.
 */
public class Version {

    private String state;//状态（0不可用：1可用）
    private String versionId;//版本编号
    private String des;//版本描述
    private String createTime;//创建时间
    private String updateTime;//更新时间

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
