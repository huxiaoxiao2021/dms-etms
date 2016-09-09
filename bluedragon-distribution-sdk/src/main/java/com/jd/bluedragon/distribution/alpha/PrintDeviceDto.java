package com.jd.bluedragon.distribution.alpha;

/**
 * Created by wuzuxiang on 2016/8/26.
 */
public class PrintDeviceDto {


    /**
     * ISVID
     */
    private String printDeviceId;

    /**
     * 版本编号
     */
    private String versionId;

    /**
     * 说明
     */
    private String des;

    /**
     * 状态
     */
    private String state;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPrintDeviceId() {
        return printDeviceId;
    }

    public void setPrintDeviceId(String printDeviceId) {
        this.printDeviceId = printDeviceId;
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
