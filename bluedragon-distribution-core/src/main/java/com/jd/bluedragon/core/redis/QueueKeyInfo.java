package com.jd.bluedragon.core.redis;

public class QueueKeyInfo {
	private String taskType;
	private String ownSign;
	private int queueId;
	private String queueKey;
	
	public QueueKeyInfo(){
		
	}

	public QueueKeyInfo(String taskType, String ownSign, int queueId,
			String queueKey) {
		super();
		this.taskType = taskType;
		this.ownSign = ownSign;
		this.queueId = queueId;
		this.queueKey = queueKey;
	}
	
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getOwnSign() {
		return ownSign;
	}
	public void setOwnSign(String ownSign) {
		this.ownSign = ownSign;
	}
	public int getQueueId() {
		return queueId;
	}
	public void setQueueId(int queueId) {
		this.queueId = queueId;
	}

	public String getQueueKey() {
		return queueKey;
	}
	public void setQueueKey(String queueKey) {
		this.queueKey = queueKey;
	}
}
