package com.jd.bluedragon.external.crossbow.postal.domain;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *     运单轨迹信息传输接口请求对象
 *
 * @author wuyoude
 **/
public class TracesCompanyRequest implements Serializable{
	private static final long serialVersionUID = 1L;
	
	/**
	 * 接入平台的标识-由快递协同信息平台提供
	 */
	private String brandCode;
	/**
	 * 轨迹列表
	 */
	private List<TracesCompanyRequestItem> traces;
	
	public String getBrandCode() {
		return brandCode;
	}
	public void setBrandCode(String brandCode) {
		this.brandCode = brandCode;
	}
	public List<TracesCompanyRequestItem> getTraces() {
		return traces;
	}
	public void setTraces(List<TracesCompanyRequestItem> traces) {
		this.traces = traces;
	}
	
}
