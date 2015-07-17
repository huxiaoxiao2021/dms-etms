package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

/**
 * PDA
 * @author zhuchao
 *
 */
public class SendMeasureRequest extends JdRequest{

	private static final long serialVersionUID = -5898848410542839979L;

	private String sendCode;

	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}
}
