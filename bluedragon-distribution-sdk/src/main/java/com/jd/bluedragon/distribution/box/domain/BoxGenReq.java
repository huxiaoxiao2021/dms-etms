package com.jd.bluedragon.distribution.box.domain;

import java.io.Serializable;

/**
 * @ClassName BoxGenReq
 * @Description
 * @Author wyh
 * @Date 2021/2/2 19:36
 **/
public class BoxGenReq implements Serializable {

    private static final long serialVersionUID = -3820264084349781124L;

    /**
     * 租户
     */
    private String tenantCode;

    /**
     * 始发网点
     */
    private Integer startSiteCode;

    /**
     * 目的网点
     */
    private Integer destSiteCode;

    /**
     * 操作人id
     */
    private Integer operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 箱号类型
     */
    private String boxType;

    /**
     * 创建箱号数量
     */
    private Integer num;

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public Integer getStartSiteCode() {
        return startSiteCode;
    }

    public void setStartSiteCode(Integer startSiteCode) {
        this.startSiteCode = startSiteCode;
    }

    public Integer getDestSiteCode() {
        return destSiteCode;
    }

    public void setDestSiteCode(Integer destSiteCode) {
        this.destSiteCode = destSiteCode;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
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
