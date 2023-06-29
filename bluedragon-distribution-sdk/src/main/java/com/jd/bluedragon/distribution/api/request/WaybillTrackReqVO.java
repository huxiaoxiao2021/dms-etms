package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

/**
 * 查询运单全程跟踪请求体
 *
 * @author hujiping
 * @date 2023/3/8 2:37 PM
 */
public class WaybillTrackReqVO implements Serializable {

    private String erp;

    private String barCode;

    public String getErp() {
        return erp;
    }

    public void setErp(String erp) {
        this.erp = erp;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
}
