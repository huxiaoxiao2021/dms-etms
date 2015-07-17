package com.jd.bluedragon.distribution.send.domain;

import java.io.Serializable;

public class SendThreeDetail implements Serializable{

	private static final long serialVersionUID = -3889610527012046508L;

	/** 箱号 */
    private String boxCode;

    /** 包裹号 */
    private String packageBarcode;

    /** 扫描标识 */
    private String mark;

    /**
     * 运单是否全
     */
    private int isWaybillFull;

	public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	public String getPackageBarcode() {
		return packageBarcode;
	}

	public void setPackageBarcode(String packageBarcode) {
		this.packageBarcode = packageBarcode;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

    public int getIsWaybillFull() {
        return isWaybillFull;
    }

    public void setIsWaybillFull(int isWaybillFull) {
        this.isWaybillFull = isWaybillFull;
    }
}