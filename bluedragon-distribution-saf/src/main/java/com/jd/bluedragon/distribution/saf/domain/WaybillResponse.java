package com.jd.bluedragon.distribution.saf.domain;

public class WaybillResponse implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -232029855122892290L;

	/**
	 * 箱号
	 * */
	private String boxCode;
	
	/**
	 * 运单号
	 * */
	private String waybillCode;
	
	/**
	 * 包裹号
	 * */
	private String packageCode;

	public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public String getPackageCode() {
		return packageCode;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((boxCode == null) ? 0 : boxCode.hashCode());
		result = prime * result
				+ ((packageCode == null) ? 0 : packageCode.hashCode());
		result = prime * result
				+ ((waybillCode == null) ? 0 : waybillCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WaybillResponse other = (WaybillResponse) obj;
		if (boxCode == null) {
			if (other.boxCode != null)
				return false;
		} else if (!boxCode.equals(other.boxCode))
			return false;
		if (packageCode == null) {
			if (other.packageCode != null)
				return false;
		} else if (!packageCode.equals(other.packageCode))
			return false;
		if (waybillCode == null) {
			if (other.waybillCode != null)
				return false;
		} else if (!waybillCode.equals(other.waybillCode))
			return false;
		return true;
	}
	
}
