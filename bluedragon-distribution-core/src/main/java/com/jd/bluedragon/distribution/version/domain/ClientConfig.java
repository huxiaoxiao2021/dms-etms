package com.jd.bluedragon.distribution.version.domain;

import java.util.Date;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.jd.bluedragon.CustomerDateSerializer;
/**
 * 客户端配置
 * @author LJZH
 *
 */
public class ClientConfig {
	/** 主键ID */
	private Long configId;
	/** 分拣中心编号 */
	private String siteCode;
	/** 应用程序类型：11-卡西欧PDA,12-方正PDA, 20-Winform客户端*/
	private Integer programType;
	/** 版本号 */
	private String versionCode;
	/** 创建时间*/
	private Date createTime;
	/** 更新时间 */
	private Date updateTime;
	/** 是否删除 '0' 删除 '1' 使用 */
	private Integer yn;

	public ClientConfig() {
	}

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
		return "ClientConfig [configId=" + configId + ", createTime="
				+ createTime + ", programType=" + programType + ", siteCode="
				+ siteCode + ", updateTime=" + updateTime + ", versionCode="
				+ versionCode + ", yn=" + yn + "]";
	}

}
