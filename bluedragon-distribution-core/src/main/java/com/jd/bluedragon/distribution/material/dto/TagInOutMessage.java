package com.jd.bluedragon.distribution.material.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lijie
 * @date 2020/5/27 18:29
 */
public class TagInOutMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 封签号
     */
    private String tagNo;

    /**
     * 分拣中心id
     */
    private Integer dmsId;

    /**
     * 分拣中心名称
     */
    private String dmsName;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 操作人erp
     */
    private String erpUserCode;

    /**
     * 操作人erp名称
     */
    private String erpUserName;

    /**
     * 入库出库类型 1：入库 2：出库
     */
    private Integer inOutType;

    public String getTagNo() {
        return tagNo;
    }

    public void setTagNo(String tagNo) {
        this.tagNo = tagNo;
    }

    public Integer getDmsId() {
        return dmsId;
    }

    public void setDmsId(Integer dmsId) {
        this.dmsId = dmsId;
    }

    public String getDmsName() {
        return dmsName;
    }

    public void setDmsName(String dmsName) {
        this.dmsName = dmsName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getErpUserCode() {
        return erpUserCode;
    }

    public void setErpUserCode(String erpUserCode) {
        this.erpUserCode = erpUserCode;
    }

    public String getErpUserName() {
        return erpUserName;
    }

    public void setErpUserName(String erpUserName) {
        this.erpUserName = erpUserName;
    }

    public Integer getInOutType() {
        return inOutType;
    }

    public void setInOutType(Integer inOutType) {
        this.inOutType = inOutType;
    }
}
