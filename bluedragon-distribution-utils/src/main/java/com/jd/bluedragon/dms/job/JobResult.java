package com.jd.bluedragon.dms.job;
/**
 * 任务执行结果
 * @author wuyoude
 *
 * @param <R> 任务实体
 */
public class JobResult<R>{
	public static int CODE_EMPTY = -1;
	public static String MSG_EMPTY = "job or jobList is empty!";
	
	public static int CODE_SUC = 1;
	public static String MSG_SUC = "suc";
	
	private int code;
	
	private String message;
	
	private R data;
	
	private long costTime;
	
	public boolean isSuc() {
		return this.code == CODE_SUC;
	}
	public void toEmpty() {
		this.code = CODE_EMPTY;
		this.message = MSG_EMPTY;
	}
	public void toSuc() {
		this.code = CODE_SUC;
		this.message = MSG_SUC;
	}
	public void toSuc(R data) {
		this.toSuc();
		this.data = data;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public R getData() {
		return data;
	}
	public void setData(R data) {
		this.data = data;
	}
	public long getCostTime() {
		return costTime;
	}
	public void setCostTime(long costTime) {
		this.costTime = costTime;
	}
}
