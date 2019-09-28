package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

/**
 * 箱号与集包编号绑定、删除操作请求对象
 */
public class BoxMaterialRelationRequset  extends JdRequest {
    private static final long serialVersionUID = 1L;

    /**
     *  箱号
     */
    private String boxCode;

    /**
     * 集包袋编号
     */
    private String materialCode;

    /**
     * 操作类型 1 绑定 2 解绑
     */
    private int bindFlag;

    /**
     * 操作人ERP
     */
    private String operatorERP;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public int getBindFlag() {
        return bindFlag;
    }

    public void setBindFlag(int bindFlag) {
        this.bindFlag = bindFlag;
    }

    public String getOperatorERP() {
        return operatorERP;
    }

    public void setOperatorERP(String operatorERP) {
        this.operatorERP = operatorERP;
    }
}
