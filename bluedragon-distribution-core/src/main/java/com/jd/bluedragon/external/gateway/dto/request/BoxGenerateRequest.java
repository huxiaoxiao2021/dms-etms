package com.jd.bluedragon.external.gateway.dto.request;

import java.io.Serializable;

/**
 * 生生成单号 入参
 * @author : xumigen
 * @date : 2020/1/2
 */
public class BoxGenerateRequest implements Serializable{
    private static final long serialVersionUID = 1L;

    //租户
    private String tenantCode;
    //始发网点
    private String startSiteCode;
    //目的网点
    private String endSiteCode;
    //操作人名称
    private String operatorName;
    //操作人id
    private Integer operatorId;
    //操作单位名称
    private String operatorUnitName;
    //箱号类型
    private String boxType;
    //一次获取的箱号数量
    private Integer num;

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

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorUnitName() {
        return operatorUnitName;
    }

    public void setOperatorUnitName(String operatorUnitName) {
        this.operatorUnitName = operatorUnitName;
    }

    public String getBoxType() {
        return boxType;
    }

    public void setBoxType(String boxType) {
        this.boxType = boxType;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
