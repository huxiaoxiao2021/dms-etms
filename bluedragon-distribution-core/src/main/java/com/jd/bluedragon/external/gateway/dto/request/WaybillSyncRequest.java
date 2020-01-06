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

    //租户
    private String tenantCode;
    //始发网点
    private String startSiteCode;
    //目的网点
    private String endSiteCode;
    //操作人id
    private String operatorId;
    //操作人名称
    private String operatorName;
    //操作单位名称
    private String operatorUnitName;
    //操作时间
    private Data operatorTime;
    //箱号
    private String boxCode;
    //运单号
    private String waybillCode;
    //包裹号
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
