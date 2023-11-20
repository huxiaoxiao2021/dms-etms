package com.jd.bluedragon.utils.easydata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("oneTableEasyDataConfig")
public class OneTableEasyDataConfig {
    @Value("${oneTable.easydata.config.apiGroupName:allinonetable2}")
    String apiGroupName;
    @Value("${oneTable.easydata.config.appToken:1ed5b6c70298be8d2ad69d6644a43443}")
    String appToken;
    @Value("${easydata.config.tenant:LEO}")
    String tenant;

    /**
     * 查询丢失一表通，倒数的场地
     */
    @Value("${easydata.config.dmsWEasyDataConfig.queryLostOneTableQuotaSite:queryLostOneTableQuotaSite}")
    String queryLostOneTableSite;


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
    
}
