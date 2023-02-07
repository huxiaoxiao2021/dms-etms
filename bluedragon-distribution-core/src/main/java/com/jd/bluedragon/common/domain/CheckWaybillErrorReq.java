package com.jd.bluedragon.common.domain;

import java.io.Serializable;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/2/4
 * @Description: 异常运单校验入参
 */
public class CheckWaybillErrorReq implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 操作条码，运单号或者包裹号
     */
    private String opeCode;

    /**
     * 操作ERP
     */
    private String erp;
    /**
     * 场地编码
     */
    private String siteCode;

    /**
     * 大区编码
     */
    private String orgId;

    public String getOpeCode() {
        return opeCode;
    }

    public void setOpeCode(String opeCode) {
        this.opeCode = opeCode;
    }

    public String getErp() {
        return erp;
    }

    public void setErp(String erp) {
        this.erp = erp;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
}
