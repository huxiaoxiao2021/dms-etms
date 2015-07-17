package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

public class ClientConfigRequest implements Serializable {

    private static final long serialVersionUID = 8003621414110275008L;
    /** 主键ID */
    private Long configId;
    /** 分拣中心编号 */
    private String siteCode;
    /** 应用程序类型：11-卡西欧PDA,12-方正PDA, 20-Winform客户端  */
    private Integer programType;
    /** 版本号 */
    private String versionCode;
    /** 创建时间 */
    private String createTime;
    /** 更新时间 */
    private String updateTime;
    /** 是否删除 '0' 删除 '1' 使用 */
    private Integer yn;

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public Integer getProgramType() {
        return programType;
    }

    public void setProgramType(Integer programType) {
        this.programType = programType;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
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
        return "ClientConfigRequest [configId=" + configId + ", createTime="
                + createTime + ", programType=" + programType + ", siteCode="
                + siteCode + ", updateTime=" + updateTime + ", versionCode="
                + versionCode + ", yn=" + yn + "]";
    }

}
