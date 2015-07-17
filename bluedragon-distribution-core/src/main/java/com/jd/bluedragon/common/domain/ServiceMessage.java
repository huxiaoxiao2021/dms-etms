package com.jd.bluedragon.common.domain;

import java.util.List;

public class ServiceMessage<T> {

	private List<T> successList;
	
	private List<T> failedList;
	
	private ServiceResultEnum result;
	
	private String errorMsg;

	public List<T> getSuccessList() {
		return successList;
	}

	public void setSuccessList(List<T> successList) {
		this.successList = successList;
	}

	public ServiceResultEnum getResult() {
		return result;
	}

	public void setResult(ServiceResultEnum result) {
		this.result = result;
	}
	
	public List<T> getFailedList() {
		return failedList;
	}

	public void setFailedList(List<T> failedList) {
		this.failedList = failedList;
	}

	public static void main(String args[]){
		System.out.println(ServiceResultEnum.FAILED.ordinal());
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

}
