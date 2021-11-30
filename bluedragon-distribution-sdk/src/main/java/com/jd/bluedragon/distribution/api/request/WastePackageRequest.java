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
    /**
     *  包裹号
     */
    private String packageCode;
    /**
     * 操作类型-1-弃件暂存 2-弃件废弃
     */
    private Integer operateType;
    /**
     * 运单类型 1-包裹类 2-信件类 
     */
    private Integer waybillType;

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

	public String getPackageCode() {
		return packageCode;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	public Integer getOperateType() {
		return operateType;
	}

	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}

	public Integer getWaybillType() {
		return waybillType;
	}

	public void setWaybillType(Integer waybillType) {
		this.waybillType = waybillType;
	}
}
