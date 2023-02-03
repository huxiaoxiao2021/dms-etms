package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date 20210825
 **/
public class TransportServiceRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操作网点编码
     */
    private Integer createSiteId;
    /**
     * 操作类型  1-装车  2-卸车
     */
    private Integer businessType;
    /**
     * erp
     */
    private String userErp;
    /**
     * 运单号
     */
    private String waybillCode;
    /**
     * 运单标位
     */
    private String waybillSign;
    /**
     * 包裹号
     */
    private String packageCode;



    public Integer getCreateSiteId() {
        return createSiteId;
    }

    public void setCreateSiteId(Integer createSiteId) {
        this.createSiteId = createSiteId;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getWaybillSign() {
        return waybillSign;
    }

    public void setWaybillSign(String waybillSign) {
        this.waybillSign = waybillSign;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }
}
