package com.jd.bluedragon.alpha.domain;

/**
 * Created by wuzuxiang on 2016/8/25.
 */
public class Version {

    /**
     * 版本状态 true启用 false停用
     */
    private boolean state;
    /**
     * 版本编号 作为UCC存储的key值
     */
    private String versionId;
    /**
     * 描述
     */
    private String des;
    /**
     * 版本创建时间
     */
    private String createTime;
    /**
     * 版本更新时间
     */
    private String updateTime;

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
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
