package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

public class ElectronSiteResponse  extends JdResponse {

	
	public static final Integer CODE_Electron_NOT_FOUND = 20101;
	public static final String MESSAGE_Electron_NOT_FOUND = "无对应任务区信息";
	public static final Integer CODE_Electron_0_FOUND = 20102;
	public static final String MESSAGE_Electron_0_FOUND = "无预分拣站点信息";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2710482658705742723L;


	/**
	 * 任务区
	 */
	private String taskAreaNo;

	/**
	 * 主机IP
	 */
	private String ip;

	public String getTaskAreaNo() {
		return taskAreaNo;
	}


	public void setTaskAreaNo(String taskAreaNo) {
		this.taskAreaNo = taskAreaNo;
	}

	/**
	 * 电子标签设备码
	 */
	private String electronNo;

 


	public String getElectronNo() {
		return electronNo;
	}


	public void setElectronNo(String electronNo) {
		this.electronNo = electronNo;
	}


	public ElectronSiteResponse(Integer code, String message){
		super(code, message);
	}
 
	public ElectronSiteResponse(){
	 
	}


	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}
	
	
}
