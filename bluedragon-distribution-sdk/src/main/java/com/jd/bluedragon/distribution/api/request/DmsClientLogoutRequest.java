package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @ClassName: DmsClientLoginRequest
 * @Description: 客户端登录请求
 * @author: wuyoude
 * @date: 2019年12月30日 下午3:10:29
 *
 */
public class DmsClientLogoutRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 客户端登录id
     */
    private Long loginId;
    /**
     * 登出方式
     */
    private Integer logoutType;
    
	 /** 登出时间 */
	private Date logoutTime;
	/**
	 * @return the loginId
	 */
	public Long getLoginId() {
		return loginId;
	}
	/**
	 * @param loginId the loginId to set
	 */
	public void setLoginId(Long loginId) {
		this.loginId = loginId;
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
	 * @return the logoutTime
	 */
	public Date getLogoutTime() {
		return logoutTime;
	}
	/**
	 * @param logoutTime the logoutTime to set
	 */
	public void setLogoutTime(Date logoutTime) {
		this.logoutTime = logoutTime;
	}
}
