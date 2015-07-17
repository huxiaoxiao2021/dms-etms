package com.jd.bluedragon.distribution.version.domain;

import java.util.Date;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.jd.bluedragon.CustomerDateSerializer;

/**
 * 客户端版本信息
 * 
 * @author LJZH
 * 
 */
public class ClientVersion {
    /** 主键ID */
    private Long versionId;
    /** 版本号 */
    private String versionCode;
    /** 版本类型：11-卡西欧PDA,12-方正PDA, 20-Winform客户端 */
    private Integer versionType;
    /** 下载地址 */
    private String downloadUrl;
    /** 版本说明 */
    private String memo;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;
    /** 是否删除 '0' 删除 '1' 使用 */
    private Integer yn;

    public ClientVersion() {
    }

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

    @JsonSerialize(using = CustomerDateSerializer.class)
    public Date getCreateTime() {
    	return createTime!=null?(Date)createTime.clone():null;
    }

    public void setCreateTime(Date createTime) {
    	this.createTime = createTime!=null?(Date)createTime.clone():null;
    }

    @JsonSerialize(using = CustomerDateSerializer.class)
    public Date getUpdateTime() {
    	return updateTime!=null?(Date)updateTime.clone():null;
    }

    public void setUpdateTime(Date updateTime) {
    	this.updateTime = updateTime!=null?(Date)updateTime.clone():null;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    @Override
    public String toString() {
        return "ClientVersion [createTime=" + createTime + ", downloadUrl="
                + downloadUrl + ", memo=" + memo + ", updateTime=" + updateTime
                + ", versionCode=" + versionCode + ", versionId=" + versionId
                + ", versionType=" + versionType + ", yn=" + yn + "]";
    }

}
