package com.jd.bluedragon.distribution.schedule.vo;

import java.io.Serializable;
/**
 * 操作日志
 * @author wuyoude
 *
 */
public class DmsEdnOperateLog implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * 操作时间
	 */
	private String operateTime;
	/**
	 * 操作人
	 */
	private String operateUser;
	/**
	 * 日志内容
	 */
	private String operateContent;
	
	public String getOperateTime() {
		return operateTime;
	}
	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}
	public String getOperateUser() {
		return operateUser;
	}
	public void setOperateUser(String operateUser) {
		this.operateUser = operateUser;
	}
	public String getOperateContent() {
		return operateContent;
	}
	public void setOperateContent(String operateContent) {
		this.operateContent = operateContent;
	}
}
