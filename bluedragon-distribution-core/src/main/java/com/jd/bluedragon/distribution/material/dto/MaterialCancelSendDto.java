package com.jd.bluedragon.distribution.material.dto;

import java.io.Serializable;

/**
 * @ClassName MaterialCancelSendDto
 * @Description
 * @Author wyh
 * @Date 2020/3/16 14:04
 **/
public class MaterialCancelSendDto implements Serializable {

    private static final long serialVersionUID = 7137201474688255361L;

    /**
     * 发货批次号
     */
    private String batchCode;

    /**
     * 操作人ERP
     */
    private String erpUserCode;

    /**
     * 操作人
     */
    private String erpUserName;

    /**
     * 操作时间
     */
    private Long operateTime;

    /**
     * 操作人所在分拣中心
     */
    private Long createSiteCode;

    /**
     * 操作人所在分拣中心名称
     */
    private String createSiteName;

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
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

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public Long getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Long createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }
}
