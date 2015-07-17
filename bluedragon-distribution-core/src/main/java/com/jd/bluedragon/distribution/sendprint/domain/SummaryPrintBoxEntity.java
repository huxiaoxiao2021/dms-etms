package com.jd.bluedragon.distribution.sendprint.domain;

import java.io.Serializable;

public class SummaryPrintBoxEntity implements Serializable {
	
	private static final long serialVersionUID = 1460940601626738146L;

	/** 箱号 */
	private String boxCode;

	/** 订单数 */
	private int waybillNum;

	/** 实发包裹数 */
	private int packageBarNum;
	
	/** 应发包裹数 */
    private int packageBarRecNum;

	/** 封签号1 */
	private String sealNo1;

	/** 封签号2 */
	private String sealNo2;

	/** 锁时间 */
	private String lockTime;
	
	public String getBoxCode() {
		return boxCode;
	}
	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}
	public int getWaybillNum() {
		return waybillNum;
	}
	public void setWaybillNum(int waybillNum) {
		this.waybillNum = waybillNum;
	}
	public int getPackageBarNum() {
		return packageBarNum;
	}
	public void setPackageBarNum(int packageBarNum) {
		this.packageBarNum = packageBarNum;
	}
	public String getSealNo1() {
		return sealNo1;
	}
	public void setSealNo1(String sealNo1) {
		this.sealNo1 = sealNo1;
	}
	public String getSealNo2() {
		return sealNo2;
	}
	public void setSealNo2(String sealNo2) {
		this.sealNo2 = sealNo2;
	}
    public int getPackageBarRecNum() {
        return packageBarRecNum;
    }
    public void setPackageBarRecNum(int packageBarRecNum) {
        this.packageBarRecNum = packageBarRecNum;
    }
    public String getLockTime() {
        return lockTime;
    }
    public void setLockTime(String lockTime) {
        this.lockTime = lockTime;
    }
}
