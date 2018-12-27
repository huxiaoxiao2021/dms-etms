package com.jd.bluedragon.distribution.handler;

import com.jd.bluedragon.distribution.command.JdResult;

/**
 * 
 * @ClassName: HandlerResult
 * @Description: 拦截处理结果
 * @author: wuyoude
 * @date: 2018年1月25日 下午11:00:07
 *
 */
public class InterceptResult<T> extends JdResult<T>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 拦截状态-1-通过
	 */
	public static final int STATUS_PASSED = 1;
	/**
	 * 拦截状态-2-弱拦截状态，一般会产生提示信息
	 */
	public static final int STATUS_WEAK_PASSED = 2;
	/**
	 * 拦截状态-3-强制拦截状态，不通过
	 */
	public static final int STATUS_NO_PASSED = 3;
	/**
	 * 拦截状态-默认为1-通过
	 */
	private int status = STATUS_PASSED;
	/**
	 * 是否通过
	 * @return
	 */
	public boolean isPassed(){
		return status <= STATUS_WEAK_PASSED;
	}
	/**
	 * 是否通过
	 * @return
	 */
	public boolean isWeakPassed(){
		return status == STATUS_WEAK_PASSED;
	}
	
	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	@Override
	public void toSuccess(int messageCode, String message) {
		this.status = STATUS_PASSED;
		super.toSuccess(messageCode, message);
	}
	public void toWeakSuccess(String message) {
		this.status = STATUS_WEAK_PASSED;
		super.toSuccess(message);
	}
	public void toWeakSuccess(int messageCode, String message) {
		this.status = STATUS_WEAK_PASSED;
		super.toSuccess(messageCode, message);
	}
	@Override
	public void toFail(int messageCode, String message) {
		this.status = STATUS_NO_PASSED;
		super.toFail(messageCode, message);
	}
	@Override
	public void toError(int messageCode, String message) {
		this.status = STATUS_NO_PASSED;
		super.toError(messageCode, message);
	}
}
