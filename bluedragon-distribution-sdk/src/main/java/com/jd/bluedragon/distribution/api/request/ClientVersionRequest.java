package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

public class ClientVersionRequest implements Serializable {

    private static final long serialVersionUID = -3580185385329400415L;
    /** 主键ID */
    private Long versionId;
    /** 版本号 */
    private String versionCode;
    /** 版本类型：11-卡西欧PDA,12-方正PDA, 20-Winform客户端  */
    private Integer versionType;
    /** 下载地址 */
    private String downloadUrl;
    /** 版本说明 */
    private String memo;
    /** 创建时间*/
    private String createTime;
    /** 更新时间 */
    private String updateTime;
    /** 是否删除 '0' 删除 '1' 使用 */
    private Integer yn;

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public Integer getVersionType() {
        return versionType;
    }

    public void setVersionType(Integer versionType) {
        this.versionType = versionType;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
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

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    @Override
    public String toString() {
        return "ClientVersionRequest [downloadUrl=" + downloadUrl + ", memo="
                + memo + ", versionCode=" + versionCode + ", versionType="
                + versionType + ", yn=" + yn + "]";
    }

}
