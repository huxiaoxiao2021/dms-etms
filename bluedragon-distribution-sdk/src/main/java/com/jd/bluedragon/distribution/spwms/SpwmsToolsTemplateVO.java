package com.jd.bluedragon.distribution.spwms;

import java.io.Serializable;

/**
 * 建箱包裹数 模板上传VO
 */
public class SpwmsToolsTemplateVO implements Serializable {

    static final long serialVersionUID = 1L;
    /**
     *
     */
    private String orgId;
    /**
     * 商品ID
     */
    private String wareId;
    /**
     * 商品数量
     */
    private String count;

    /**
     * 订单号
     */
    private String orderId;
    /**
     * 备件库ID
     */
    private String spwmsId;
    /**
     * 备件条码
     */
    private String spCode;
    /**
     * 正向运单
     */
    private String oldWaybillCode;
    /**
     * 逆向运单
     */
    private String newWaybillCode;


    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getWareId() {
        return wareId;
    }

    public void setWareId(String wareId) {
        this.wareId = wareId;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSpwmsId() {
        return spwmsId;
    }

    public void setSpwmsId(String spwmsId) {
        this.spwmsId = spwmsId;
    }

    public String getSpCode() {
        return spCode;
    }

    public void setSpCode(String spCode) {
        this.spCode = spCode;
    }

    public String getOldWaybillCode() {
        return oldWaybillCode;
    }

    public void setOldWaybillCode(String oldWaybillCode) {
        this.oldWaybillCode = oldWaybillCode;
    }

    public String getNewWaybillCode() {
        return newWaybillCode;
    }

    public void setNewWaybillCode(String newWaybillCode) {
        this.newWaybillCode = newWaybillCode;
    }
}
