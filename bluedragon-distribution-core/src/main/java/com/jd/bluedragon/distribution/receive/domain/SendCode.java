package com.jd.bluedragon.distribution.receive.domain;

public class SendCode {
 private String boxCode;
 private String sendCode;
public String getBoxCode() {
	return boxCode;
}
public void setBoxCode(String boxCode) {
	this.boxCode = boxCode;
}
public String getSendCode() {
	return sendCode;
}
public void setSendCode(String sendCode) {
	this.sendCode = sendCode;
}
@Override
public String toString() {
	return "SendCode [boxCode=" + boxCode + ", sendCode=" + sendCode + "]";
}
}
