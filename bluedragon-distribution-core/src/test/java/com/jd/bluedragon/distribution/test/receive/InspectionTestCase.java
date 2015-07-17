package com.jd.bluedragon.distribution.test.receive;

import org.junit.Test;

import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;

public class InspectionTestCase {
	
	public Integer orgId;
	public Integer cky2;
	public Integer storeId;
	public Long orderId;
	public String packageCode;
	public String operateTime;
	public String operator;
	public String fingerprint;

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getPackageCode() {
		return packageCode;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	public String getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getFingerprint() {
		return fingerprint;
	}

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}
	

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public Integer getCky2() {
		return cky2;
	}

	public void setCky2(Integer cky2) {
		this.cky2 = cky2;
	}

	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	@Test
	public void test() {
		InspectionTestCase model = new InspectionTestCase();
		model.setOrgId(6);
		model.setCky2(6);
		model.setStoreId(0);
		model.setOrderId(793435640L);
		model.setOperateTime("2013-10-19 11:15:00");
		model.setOperator("bjly|李义");
		model.setPackageCode("793435640-1-1-1");
		model.setFingerprint(Md5Helper.encode(model.getOperateTime() + model.getOperator() + model.getOrderId()
				+ model.getPackageCode()));

		System.out.println(JsonHelper.toJson(model));
	}

}
