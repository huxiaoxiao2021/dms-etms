package com.jd.bluedragon.common.dto.sendcode.response;

import java.io.Serializable;

public class BatchSendCarInfoDto implements Serializable {

	private static final long serialVersionUID = 6924730647940585634L;



	/** 批次号 */
	private String sendCode;

	/** 箱数 */
	private int totalBoxNum=0;

	/** 原包总数 */
	private int packageBarNum=0;

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public int getTotalBoxNum() {
        return totalBoxNum;
    }

    public void setTotalBoxNum(int totalBoxNum) {
        this.totalBoxNum = totalBoxNum;
    }

    public int getPackageBarNum() {
        return packageBarNum;
    }

    public void setPackageBarNum(int packageBarNum) {
        this.packageBarNum = packageBarNum;
    }
}
