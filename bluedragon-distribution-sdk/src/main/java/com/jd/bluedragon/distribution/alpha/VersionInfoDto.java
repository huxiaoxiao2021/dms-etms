package com.jd.bluedragon.distribution.alpha;

/**
 * Created by wuzuxiang on 2016/8/26.
 */
public class VersionInfoDto {

    /**
     * 版本编号
     */
    private String versionId;

    /**
     * 文件上传路径
     */
    private String file;

    /**
     * 版本说明
     */
    private String des;

    /**
     * 状态
     */
    private String state;

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
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
}
