package com.jd.bluedragon.external.crossbow.postal.domain;

/**
 * <p>
 *     运单信息传输接口返回对象
 *
 * @author wuyoude
 **/
public class WaybillInfoResponse extends EmsBaseResponse{
	private static final long serialVersionUID = 1L;
    /**
     * 运单号
     */
	private String waybillNo;
	
	public String getWaybillNo() {
		return waybillNo;
	}
	public void setWaybillNo(String waybillNo) {
		this.waybillNo = waybillNo;
	}
	
}
