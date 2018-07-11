package com.jd.bluedragon.distribution.api.request;

import com.jd.ql.dms.common.domain.JdRequest;

/**
 * 逆向返仓操作请求体
 */
public class ReverseWarehouseRequest extends JdRequest {

    private static final long serialVersionUID = 556595029682781842L;

    /** 包裹号或运单号 */
    private String packageOrWaybillCode;

    public String getPackageOrWaybillCode() {
        return packageOrWaybillCode;
    }

    public void setPackageOrWaybillCode(String packageOrWaybillCode) {
        this.packageOrWaybillCode = packageOrWaybillCode;
    }
}
