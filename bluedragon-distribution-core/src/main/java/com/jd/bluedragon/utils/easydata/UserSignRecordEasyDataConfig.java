package com.jd.bluedragon.utils.easydata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("userSignRecordEasyDataConfig")
public class UserSignRecordEasyDataConfig {
	/**
	 * 最大签到时间
	 */
	@Value("${easydata.config.apiGroupName:work_station_dim}")
    String apiGroupName;
	@Value("${easydata.config.appToken:c2a2f5f245cf0e67558f04304606bffd}")
    String appToken;
	@Value("${easydata.config.tenant:1}")
    String tenant;
    
	@Value("${easydata.config.userSignRecordEasyDataConfig.querySignCount:queryCountForFlow}")	
    String queryCountForFlow;
	@Value("${easydata.config.userSignRecordEasyDataConfig.querySignList:queryDataListForFlow}")	
    String queryDataListForFlow;
	@Value("${easydata.config.userSignRecordEasyDataConfig.queryById:queryByIdForFlow}")	
    String queryByIdForFlow;
	@Value("${easydata.config.userSignRecordEasyDataConfig.queryCountForCheckSignTime:queryCountForCheckSignTime}")	
    String queryCountForCheckSignTime;
	public String getApiGroupName() {
		return apiGroupName;
	}
	public void setApiGroupName(String apiGroupName) {
		this.apiGroupName = apiGroupName;
	}
	public String getAppToken() {
		return appToken;
	}
	public void setAppToken(String appToken) {
		this.appToken = appToken;
	}
	public String getTenant() {
		return tenant;
	}
	public void setTenant(String tenant) {
		this.tenant = tenant;
	}
	public String getQueryCountForFlow() {
		return queryCountForFlow;
	}
	public void setQueryCountForFlow(String queryCountForFlow) {
		this.queryCountForFlow = queryCountForFlow;
	}
	public String getQueryDataListForFlow() {
		return queryDataListForFlow;
	}
	public void setQueryDataListForFlow(String queryDataListForFlow) {
		this.queryDataListForFlow = queryDataListForFlow;
	}
	public String getQueryByIdForFlow() {
		return queryByIdForFlow;
	}
	public void setQueryByIdForFlow(String queryByIdForFlow) {
		this.queryByIdForFlow = queryByIdForFlow;
	}
	public String getQueryCountForCheckSignTime() {
		return queryCountForCheckSignTime;
	}
	public void setQueryCountForCheckSignTime(String queryCountForCheckSignTime) {
		this.queryCountForCheckSignTime = queryCountForCheckSignTime;
	}
    
}
