package com.jd.bluedragon.distribution.api.request;


import com.jd.bluedragon.distribution.api.JdRequest;

public class AbnormalOrderRequest  extends JdRequest {
	String orderId;
	Integer abnormalCode1;
	String abnormalReason1;
	Integer abnormalCode2;
	String abnormalReason2;
	String createUserErp;
	String trackContent;  // 全程跟踪显示内容
	String waveBusinessId;//版次号，路由系统的字段

	public String getWaveBusinessId() {
		return waveBusinessId;
	}

	public void setWaveBusinessId(String waveBusinessId) {
		this.waveBusinessId = waveBusinessId;
	}

	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public Integer getAbnormalCode1() {
		return abnormalCode1;
	}
	public void setAbnormalCode1(Integer abnormalCode1) {
		this.abnormalCode1 = abnormalCode1;
	}
	public String getAbnormalReason1() {
		return abnormalReason1;
	}
	public void setAbnormalReason1(String abnormalReason1) {
		this.abnormalReason1 = abnormalReason1;
	}
	public Integer getAbnormalCode2() {
		return abnormalCode2;
	}
	public void setAbnormalCode2(Integer abnormalCode2) {
		this.abnormalCode2 = abnormalCode2;
	}
	public String getAbnormalReason2() {
		return abnormalReason2;
	}
	public void setAbnormalReason2(String abnormalReason2) {
		this.abnormalReason2 = abnormalReason2;
	}
	public String getCreateUserErp() {
		return createUserErp;
	}
	public void setCreateUserErp(String createUserErp) {
		this.createUserErp = createUserErp;
	}

	public String getTrackContent() {
		return trackContent;
	}

	public void setTrackContent(String trackContent) {
		this.trackContent = trackContent;
	}
}
