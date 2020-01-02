package com.jd.bluedragon.external.gateway.dto.request;

import javax.xml.crypto.Data;
import java.io.Serializable;

/**
 * 运单与箱号关系绑定 请求对象
 * @author : xumigen
 * @date : 2020/1/2
 */
public class WaybillSyncRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tenantCode;
    private String startSiteCode;
    private String endSiteCode;
    private String operatorId;
    private String operatorName;
    private String operatorUnitName;
    private Data operatorTime;
    private String boxCode;
    private String waybillCode;
    private String packageCode;

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getStartSiteCode() {
        return startSiteCode;
    }

    public void setStartSiteCode(String startSiteCode) {
        this.startSiteCode = startSiteCode;
    }

    public String getEndSiteCode() {
        return endSiteCode;
    }

    public void setEndSiteCode(String endSiteCode) {
        this.endSiteCode = endSiteCode;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperatorUnitName() {
        return operatorUnitName;
    }

    public void setOperatorUnitName(String operatorUnitName) {
        this.operatorUnitName = operatorUnitName;
    }

    public Data getOperatorTime() {
        return operatorTime;
    }

    public void setOperatorTime(Data operatorTime) {
        this.operatorTime = operatorTime;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }
}
