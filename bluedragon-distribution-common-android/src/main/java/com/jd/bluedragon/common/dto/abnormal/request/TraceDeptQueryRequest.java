package com.jd.bluedragon.common.dto.abnormal.request;

import java.io.Serializable;

public class TraceDeptQueryRequest implements Serializable{
	private static final long serialVersionUID = 1L;
	private String code;
	private String currentDept;
	private Long firstLevelExceptionId;
	private String firstLevelExceptionName;
	private Long secondLevelExceptionId;
	private String secondLevelExceptionName;
	private Long thirdLevelExceptionId;
	private String thirdLevelExceptionName;
	private String specialScene;
	private Long thirdLevelReasonId;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCurrentDept() {
		return currentDept;
	}
	public void setCurrentDept(String currentDept) {
		this.currentDept = currentDept;
	}
	public Long getFirstLevelExceptionId() {
		return firstLevelExceptionId;
	}
	public void setFirstLevelExceptionId(Long firstLevelExceptionId) {
		this.firstLevelExceptionId = firstLevelExceptionId;
	}
	public String getFirstLevelExceptionName() {
		return firstLevelExceptionName;
	}
	public void setFirstLevelExceptionName(String firstLevelExceptionName) {
		this.firstLevelExceptionName = firstLevelExceptionName;
	}
	public Long getSecondLevelExceptionId() {
		return secondLevelExceptionId;
	}
	public void setSecondLevelExceptionId(Long secondLevelExceptionId) {
		this.secondLevelExceptionId = secondLevelExceptionId;
	}
	public String getSecondLevelExceptionName() {
		return secondLevelExceptionName;
	}
	public void setSecondLevelExceptionName(String secondLevelExceptionName) {
		this.secondLevelExceptionName = secondLevelExceptionName;
	}
	public Long getThirdLevelExceptionId() {
		return thirdLevelExceptionId;
	}
	public void setThirdLevelExceptionId(Long thirdLevelExceptionId) {
		this.thirdLevelExceptionId = thirdLevelExceptionId;
	}
	public String getThirdLevelExceptionName() {
		return thirdLevelExceptionName;
	}
	public void setThirdLevelExceptionName(String thirdLevelExceptionName) {
		this.thirdLevelExceptionName = thirdLevelExceptionName;
	}
	public String getSpecialScene() {
		return specialScene;
	}
	public void setSpecialScene(String specialScene) {
		this.specialScene = specialScene;
	}
	public Long getThirdLevelReasonId() {
		return thirdLevelReasonId;
	}
	public void setThirdLevelReasonId(Long thirdLevelReasonId) {
		this.thirdLevelReasonId = thirdLevelReasonId;
	}
}
