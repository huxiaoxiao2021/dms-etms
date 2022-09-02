package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @ClassName: DmsClientHeartbeatRequest
 * @Description: 客户端心跳请求 从business工程中迁移过来
 * @author: wuyoude
 * @date: 2019年12月30日 下午3:10:29
 *
 */
public class DmsClientHeartbeatRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 客户端登录id
     */
    private Long loginId;
    
	 /** 客户端请求时间 */
	private Date requestTime;
    
	 /** 心跳时间 */
	private Date heartbeatTime;
    
	 /** 客户端运行环境 */
	private String runningMode;

	 /** 客户端运行状态,记录当前客户端状态：1-运行、2-下载升级中、3-待升级 */
	private Integer runningStatus;

	/**
	 * 客户端的网络地址
	 */
	private String gisAddress;

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
	 * @return the requestTime
	 */
	public Date getRequestTime() {
		return requestTime;
	}

	/**
	 * @param requestTime the requestTime to set
	 */
	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}

	/**
	 * @return the heartbeatTime
	 */
	public Date getHeartbeatTime() {
		return heartbeatTime;
	}

	/**
	 * @param heartbeatTime the heartbeatTime to set
	 */
	public void setHeartbeatTime(Date heartbeatTime) {
		this.heartbeatTime = heartbeatTime;
	}

	/**
	 * @return the runningMode
	 */
	public String getRunningMode() {
		return runningMode;
	}

	/**
	 * @param runningMode the runningMode to set
	 */
	public void setRunningMode(String runningMode) {
		this.runningMode = runningMode;
	}

	/**
	 * @return the runningStatus
	 */
	public Integer getRunningStatus() {
		return runningStatus;
	}

	/**
	 * @param runningStatus the runningStatus to set
	 */
	public void setRunningStatus(Integer runningStatus) {
		this.runningStatus = runningStatus;
	}

	public String getGisAddress() {
		return gisAddress;
	}

	public void setGisAddress(String gisAddress) {
		this.gisAddress = gisAddress;
	}
}
