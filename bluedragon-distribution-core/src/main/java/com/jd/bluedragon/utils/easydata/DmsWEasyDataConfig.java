package com.jd.bluedragon.utils.easydata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("dmsWEasyDataConfig")
public class DmsWEasyDataConfig {
    @Value("${dmsw.easydata.config.apiGroupName:dmsW}")
    String apiGroupName;
    @Value("${dmsw.easydata.config.appToken:420031492ac5be1fe540eb9a534db68f}")
    String appToken;
    @Value("${dmsw.easydata.config.tenant:ARIES}")
    String tenant;
    /**
     * 查询根据场地查询装车质量网格
     */
    @Value("${easydata.config.dmsWEasyDataConfig.queryLoadCarQualityGrid:queryLoadCarQualityGrid}")
    String queryLoadCarQualityGrid;

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
    
    public String getQueryLoadCarQualityGrid() {
        return queryLoadCarQualityGrid;
    }

    public void setQueryLoadCarQualityGrid(String queryLoadCarQualityGrid) {
        this.queryLoadCarQualityGrid = queryLoadCarQualityGrid;
    }
}
