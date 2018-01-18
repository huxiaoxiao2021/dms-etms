package com.jd.bluedragon.distribution.sysloginlog.domain;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @ClassName: SysLoginLog
 * @Description: 系统登录信息表-实体类
 * @author wuyoude
 * @date 2018年01月16日 14:54:02
 *
 */
public class SysLoginLog implements Serializable {

	private static final long serialVersionUID = 1L;

	 /** 主键ID */
	private Long id;

	 /** 操作人编码 */
	private Long loginUserCode;

	 /** 操作人ERP */
	private String loginUserErp;

	 /** 操作人名字 */
	private String loginUserName;

	 /** 登录人绑定的分拣中心ID */
	private Long dmsSiteCode;

	 /** 登录人绑定的分拣中心站点名称 */
	private String dmsSiteName;

	 /** 登录人绑定的分拣中心编号 */
	private Integer dmsSiteType;

	 /** 登录人绑定的分拣中心编号 */
	private Integer dmsSiteSubtype;

	 /** 站点编码 */
	private Long siteCode;

	 /** 站点名称 */
	private String siteName;

	 /** 应用程序类型（0-PDA,1-Winform） */
	private Integer programType;

	 /** 版本号:20180104WM */
	private String versionCode;

	 /** 版本名称:BJ_MAJUQIAO */
	private String versionName;

	 /** 文件版本号 */
	private String fileVersions;

	 /** 和客户端配置是否匹配 */
	private Integer matchFlag;

	 /** ipv4 */
	private String ipv4;

	 /** ipv6 */
	private String ipv6;

	 /** 主机名称 */
	private String machineName;

	 /** mac地址 */
	private String macAdress;

	 /** 登录时间 */
	private Date loginTime;

	 /** 创建时间 */
	private Date createTime;

	 /** 更新时间 */
	private Date updateTime;

	 /** 默认为0 */
	private Integer isDelete;

	 /** 数据库时间 */
	private Date ts;

	/**
	 * The set method for id.
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * The get method for id.
	 * @return this.id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * The set method for loginUserCode.
	 * @param loginUserCode
	 */
	public void setLoginUserCode(Long loginUserCode) {
		this.loginUserCode = loginUserCode;
	}

	/**
	 * The get method for loginUserCode.
	 * @return this.loginUserCode
	 */
	public Long getLoginUserCode() {
		return this.loginUserCode;
	}

	/**
	 * The set method for loginUserErp.
	 * @param loginUserErp
	 */
	public void setLoginUserErp(String loginUserErp) {
		this.loginUserErp = loginUserErp;
	}

	/**
	 * The get method for loginUserErp.
	 * @return this.loginUserErp
	 */
	public String getLoginUserErp() {
		return this.loginUserErp;
	}

	/**
	 * The set method for loginUserName.
	 * @param loginUserName
	 */
	public void setLoginUserName(String loginUserName) {
		this.loginUserName = loginUserName;
	}

	/**
	 * The get method for loginUserName.
	 * @return this.loginUserName
	 */
	public String getLoginUserName() {
		return this.loginUserName;
	}

	/**
	 * The set method for dmsSiteCode.
	 * @param dmsSiteCode
	 */
	public void setDmsSiteCode(Long dmsSiteCode) {
		this.dmsSiteCode = dmsSiteCode;
	}

	/**
	 * The get method for dmsSiteCode.
	 * @return this.dmsSiteCode
	 */
	public Long getDmsSiteCode() {
		return this.dmsSiteCode;
	}

	/**
	 * The set method for dmsSiteName.
	 * @param dmsSiteName
	 */
	public void setDmsSiteName(String dmsSiteName) {
		this.dmsSiteName = dmsSiteName;
	}

	/**
	 * The get method for dmsSiteName.
	 * @return this.dmsSiteName
	 */
	public String getDmsSiteName() {
		return this.dmsSiteName;
	}

	/**
	 * The set method for dmsSiteType.
	 * @param dmsSiteType
	 */
	public void setDmsSiteType(Integer dmsSiteType) {
		this.dmsSiteType = dmsSiteType;
	}

	/**
	 * The get method for dmsSiteType.
	 * @return this.dmsSiteType
	 */
	public Integer getDmsSiteType() {
		return this.dmsSiteType;
	}

	/**
	 * The set method for dmsSiteSubtype.
	 * @param dmsSiteSubtype
	 */
	public void setDmsSiteSubtype(Integer dmsSiteSubtype) {
		this.dmsSiteSubtype = dmsSiteSubtype;
	}

	/**
	 * The get method for dmsSiteSubtype.
	 * @return this.dmsSiteSubtype
	 */
	public Integer getDmsSiteSubtype() {
		return this.dmsSiteSubtype;
	}

	/**
	 * The set method for siteCode.
	 * @param siteCode
	 */
	public void setSiteCode(Long siteCode) {
		this.siteCode = siteCode;
	}

	/**
	 * The get method for siteCode.
	 * @return this.siteCode
	 */
	public Long getSiteCode() {
		return this.siteCode;
	}

	/**
	 * The set method for siteName.
	 * @param siteName
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	/**
	 * The get method for siteName.
	 * @return this.siteName
	 */
	public String getSiteName() {
		return this.siteName;
	}

	/**
	 * The set method for programType.
	 * @param programType
	 */
	public void setProgramType(Integer programType) {
		this.programType = programType;
	}

	/**
	 * The get method for programType.
	 * @return this.programType
	 */
	public Integer getProgramType() {
		return this.programType;
	}

	/**
	 * The set method for versionCode.
	 * @param versionCode
	 */
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	/**
	 * The get method for versionCode.
	 * @return this.versionCode
	 */
	public String getVersionCode() {
		return this.versionCode;
	}

	/**
	 * The set method for versionName.
	 * @param versionName
	 */
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	/**
	 * The get method for versionName.
	 * @return this.versionName
	 */
	public String getVersionName() {
		return this.versionName;
	}

	/**
	 * The set method for fileVersions.
	 * @param fileVersions
	 */
	public void setFileVersions(String fileVersions) {
		this.fileVersions = fileVersions;
	}

	/**
	 * The get method for fileVersions.
	 * @return this.fileVersions
	 */
	public String getFileVersions() {
		return this.fileVersions;
	}

	/**
	 * The set method for matchFlag.
	 * @param matchFlag
	 */
	public void setMatchFlag(Integer matchFlag) {
		this.matchFlag = matchFlag;
	}

	/**
	 * The get method for matchFlag.
	 * @return this.matchFlag
	 */
	public Integer getMatchFlag() {
		return this.matchFlag;
	}

	/**
	 * The set method for ipv4.
	 * @param ipv4
	 */
	public void setIpv4(String ipv4) {
		this.ipv4 = ipv4;
	}

	/**
	 * The get method for ipv4.
	 * @return this.ipv4
	 */
	public String getIpv4() {
		return this.ipv4;
	}

	/**
	 * The set method for ipv6.
	 * @param ipv6
	 */
	public void setIpv6(String ipv6) {
		this.ipv6 = ipv6;
	}

	/**
	 * The get method for ipv6.
	 * @return this.ipv6
	 */
	public String getIpv6() {
		return this.ipv6;
	}

	/**
	 * The set method for machineName.
	 * @param machineName
	 */
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	/**
	 * The get method for machineName.
	 * @return this.machineName
	 */
	public String getMachineName() {
		return this.machineName;
	}

	/**
	 * The set method for macAdress.
	 * @param macAdress
	 */
	public void setMacAdress(String macAdress) {
		this.macAdress = macAdress;
	}

	/**
	 * The get method for macAdress.
	 * @return this.macAdress
	 */
	public String getMacAdress() {
		return this.macAdress;
	}

	/**
	 * The set method for loginTime.
	 * @param loginTime
	 */
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	/**
	 * The get method for loginTime.
	 * @return this.loginTime
	 */
	public Date getLoginTime() {
		return this.loginTime;
	}

	/**
	 * The set method for createTime.
	 * @param createTime
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * The get method for createTime.
	 * @return this.createTime
	 */
	public Date getCreateTime() {
		return this.createTime;
	}

	/**
	 * The set method for updateTime.
	 * @param updateTime
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * The get method for updateTime.
	 * @return this.updateTime
	 */
	public Date getUpdateTime() {
		return this.updateTime;
	}

	/**
	 * The set method for isDelete.
	 * @param isDelete
	 */
	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

	/**
	 * The get method for isDelete.
	 * @return this.isDelete
	 */
	public Integer getIsDelete() {
		return this.isDelete;
	}

	/**
	 * The set method for ts.
	 * @param ts
	 */
	public void setTs(Date ts) {
		this.ts = ts;
	}

	/**
	 * The get method for ts.
	 * @return this.ts
	 */
	public Date getTs() {
		return this.ts;
	}


}
