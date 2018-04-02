package com.jd.bluedragon.distribution.reverse.domain;

/**
 * Created by liuduo
 * CLPS订单发送mq model
 */
public class ReverseSendMQToCLPS {

    private String jdOrderCode;
    private String sendCode;
    private String sourceCode;
    private String waybillCode;
    private Integer rejType;
    private String rejRemark;

    public String getJdOrderCode() {
        return jdOrderCode;
    }

    public void setJdOrderCode(String jdOrderCode) {
        this.jdOrderCode = jdOrderCode;
    }

    public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getRejType() {
        return rejType;
    }

    public void setRejType(Integer rejType) {
        this.rejType = rejType;
    }

    public String getRejRemark() {
        return rejRemark;
    }

    public void setRejRemark(String rejRemark) {
        this.rejRemark = rejRemark;
    }
}
