package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * 
* 类描述： 
* 创建者： libin
* 项目名称： bluedragon-distribution-sdk
* 创建时间： 2013-1-16 上午11:35:35
* 版本号： v1.0
 */
public class PopQueueResponse extends JdResponse{
	/**
	* 
	*/
	private static final long serialVersionUID = 1195311002949428590L;
	public String expressCode;
	public String expressName;
	public String queueNo;
	public Integer queueType;
	public String operateTime;
	public String getExpressCode() {
		return expressCode;
	}
	public void setExpressCode(String expressCode) {
		this.expressCode = expressCode;
	}
	public String getExpressName() {
		return expressName;
	}
	public void setExpressName(String expressName) {
		this.expressName = expressName;
	}
	
	public String getQueueNo() {
		return queueNo;
	}
	public void setQueueNo(String queueNo) {
		this.queueNo = queueNo;
	}
	public String getOperateTime() {
		return operateTime;
	}
	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}
	public Integer getQueueType() {
		return queueType;
	}
	public void setQueueType(Integer queueType) {
		this.queueType = queueType;
	}


}
