package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

/**
 * 弃件暂存请求对象
 */
public class WastePackageRequest extends JdRequest {
    private static final long serialVersionUID = 1L;

    /**
     *  运单号
     */
    private String waybillCode;

    /**
     * 状态  0 弃件暂存 1 弃件出库 2 已认领
     */
    private int status;

    /**
     * 操作人ERP
     */
    private String operatorERP;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getOperatorERP() {
        return operatorERP;
    }

    public void setOperatorERP(String operatorERP) {
        this.operatorERP = operatorERP;
    }
}
