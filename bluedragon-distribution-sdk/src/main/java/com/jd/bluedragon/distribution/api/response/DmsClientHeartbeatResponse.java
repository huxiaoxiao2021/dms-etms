package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.domain.DmsClientConfigInfo;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @ClassName: DmsClientLoginResponse
 * @Description: 客户端心跳返回
 * @author: wuyoude
 * @date: 2019年12月30日 下午3:10:29
 *
 */
public class DmsClientHeartbeatResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
	 /** 客户端登录状态：1-正常、2-账号在其他设备登录，需要提醒退出、3-版本过低，需要提醒退出、4-无心跳自动更新成退出 */
	private Integer loginStatus;
	
	 /** 应用程序类型（10、20、30-PDA,40-打印客户端,42-打印组件,43-标签设计器,60-安卓PDA） */
	private Integer programType;
	
	 /** 机构|一级机构编码 */
	private String orgCode;
	
	 /** 站点|二级机构编码 */
	private String siteCode;
	
	 /** 用户编码 */
	private String userCode;
	
    /**
     * 服务器时间
     */
	private Date serverTime;
	/**
	 * 强制退出标识
	 */
	private Boolean forceLogout;
	/**
	 * 需要提示退出时，退出的类型
	 */
	private Integer logoutType;
	
	 /** 客户端配置信息 */
	private DmsClientConfigInfo dmsClientConfigInfo;

	/**
	 * @return the loginStatus
	 */
	public Integer getLoginStatus() {
		return loginStatus;
	}

	/**
	 * @param loginStatus the loginStatus to set
	 */
	public void setLoginStatus(Integer loginStatus) {
		this.loginStatus = loginStatus;
	}

	/**
	 * @return the programType
	 */
	public Integer getProgramType() {
		return programType;
	}

	/**
	 * @param programType the programType to set
	 */
	public void setProgramType(Integer programType) {
		this.programType = programType;
	}

	/**
	 * @return the orgCode
	 */
	public String getOrgCode() {
		return orgCode;
	}

	/**
	 * @param orgCode the orgCode to set
	 */
	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	/**
	 * @return the siteCode
	 */
	public String getSiteCode() {
		return siteCode;
	}

	/**
	 * @param siteCode the siteCode to set
	 */
	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

	/**
	 * @return the userCode
	 */
	public String getUserCode() {
		return userCode;
	}

	/**
	 * @param userCode the userCode to set
	 */
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	/**
	 * @return the serverTime
	 */
	public Date getServerTime() {
		return serverTime;
	}

	/**
	 * @param serverTime the serverTime to set
	 */
	public void setServerTime(Date serverTime) {
		this.serverTime = serverTime;
	}

	/**
	 * @return the forceLogout
	 */
	public Boolean getForceLogout() {
		return forceLogout;
	}

	/**
	 * @param forceLogout the forceLogout to set
	 */
	public void setForceLogout(Boolean forceLogout) {
		this.forceLogout = forceLogout;
	}

	/**
	 * @return the logoutType
	 */
	public Integer getLogoutType() {
		return logoutType;
	}

	/**
	 * @param logoutType the logoutType to set
	 */
	public void setLogoutType(Integer logoutType) {
		this.logoutType = logoutType;
	}

	/**
	 * @return the dmsClientConfigInfo
	 */
	public DmsClientConfigInfo getDmsClientConfigInfo() {
		return dmsClientConfigInfo;
	}

	/**
	 * @param dmsClientConfigInfo the dmsClientConfigInfo to set
	 */
	public void setDmsClientConfigInfo(DmsClientConfigInfo dmsClientConfigInfo) {
		this.dmsClientConfigInfo = dmsClientConfigInfo;
	}
	
}
