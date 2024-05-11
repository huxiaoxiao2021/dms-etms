package com.jd.bluedragon.common.dto.waybill.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 *
 *  根据运单号查询运单路由下一个节点
 */
public class WaybillRouterReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 运单号
     */
    private String waybillCode;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }
}
