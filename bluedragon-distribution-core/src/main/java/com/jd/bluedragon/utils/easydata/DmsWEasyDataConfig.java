package com.jd.bluedragon.utils.easydata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("dmsWEasyDataConfig")
public class DmsWEasyDataConfig {
    @Value("${easydata.config.apiGroupName:work_station_dim}")
    String apiGroupName;
    @Value("${easydata.config.appToken:c2a2f5f245cf0e67558f04304606bffd}")
    String appToken;
    @Value("${easydata.config.tenant:1}")
    String tenant;
    /**
     * 查询丢失一表通，倒数的场地
     */
    @Value("${easydata.config.dmsWEasyDataConfig.queryLostOneTableQuotaSite:queryLostOneTableQuotaSite}")
    String queryLostOneTableSite;
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

    public String getQueryLostOneTableSite() {
        return queryLostOneTableSite;
    }

    public void setQueryLostOneTableSite(String queryLostOneTableSite) {
        this.queryLostOneTableSite = queryLostOneTableSite;
    }

    public String getQueryLoadCarQualityGrid() {
        return queryLoadCarQualityGrid;
    }

    public void setQueryLoadCarQualityGrid(String queryLoadCarQualityGrid) {
        this.queryLoadCarQualityGrid = queryLoadCarQualityGrid;
    }
}
