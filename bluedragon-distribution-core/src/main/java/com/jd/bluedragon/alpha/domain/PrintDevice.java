package com.jd.bluedragon.alpha.domain;

import java.io.Serializable;

/**
 * Created by wuzuxiang on 2016/8/22.打印插件ISV的实体类
 */
public class PrintDevice implements Serializable{

    private static final long serializable = 1L;

    private String printDeviceId;
    private String state;//ISV的状态，0停用：1启用；
    private String versionId;//版本编号
    private String des;//说明；
    private String createTime;//创建时间 yyyy-mm-dd hh:mm:ss
    private String updateTime;//更新时间 yyyy-mm-dd hh:mm:ss

    public PrintDevice() {
    }

    public PrintDevice(String printDeviceId, String state, String versionId, String des) {
        this.printDeviceId = printDeviceId;
        this.state = state;
        this.versionId = versionId;
        this.des = des;
    }

    public String getPrintDeviceId() {
        return printDeviceId;
    }

    public void setPrintDeviceId(String printDeviceId) {
        this.printDeviceId = printDeviceId;
    }

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
