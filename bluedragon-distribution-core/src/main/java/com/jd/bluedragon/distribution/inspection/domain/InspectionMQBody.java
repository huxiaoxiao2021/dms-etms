package com.jd.bluedragon.distribution.inspection.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by wangtingwei on 2016/2/22.
 */
public class InspectionMQBody implements Serializable {
    private static final long serialVersionUID = 0L;

    /**
     * 验货站点编号
     */
    private Integer inspectionSiteCode;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 操作人编码
     */
    private Integer createUserCode;

    /**
     * 操作人名称
     */
    private String createUserName;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 系统来源
     */
    private String source;


    public Integer getInspectionSiteCode() {
        return inspectionSiteCode;
    }

    public void setInspectionSiteCode(Integer inspectionSiteCode) {
        this.inspectionSiteCode = inspectionSiteCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
