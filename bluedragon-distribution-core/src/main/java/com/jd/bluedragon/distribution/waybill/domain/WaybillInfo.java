package com.jd.bluedragon.distribution.waybill.domain;

public class WaybillInfo {
	
	/**
	 * 京东订单号 例如1234567
	 * */
	private String waybillCode;
	
	/**
	 * 京东包裹号 每个订单可能对应多个包裹  1234567-1（当前的第几个包裹）-3（总的订单包裹数）-1  1234567-1-3-1
	 * */
	private String packageBarcode;
	
	/**
	 * 订单包裹数
	 * */
	private Integer packageNum;
	
	/**
	 * 大客户号
	 * */
	private String sysAccount;
	
	/**
	 * 寄件人姓名
	 * */
	private String scontactor;
	
	/**
	 * 寄件人联系方式1
	 * */
	private String scustMobile;
	
	/**
	 * 寄件人联系方式2(选填)
	 * */
	private String scustTelplus;
	
	/**
	 * 寄件人邮编
	 * */
	private String scustPost;
	
	/**
	 * 寄件人地址
	 * */
	private String scustAddr;
	
	/**
	 * 寄件人公司
	 * */
	private String scustComp;
	
	/**
	 * 收件人姓名
	 * */
	private String tcontactor;
	
	/**
	 * 收件人联系方式1
	 * */
	private String tcustMobile;
	
	/**
	 * 收件人联系方式2(选填)
	 * */
	private String tcustTelplus;
	
	/**
	 * 收件人邮编
	 * */
	private String tcustPost;
	
	/**
	 * 收件人地址
	 * */
	private String tcustAddr;
	
	/**
	 * 收件人公司
	 * */
	private String tcustComp;
	
	/**
	 * 到件省
	 * */
	private String tcustProvince;
	
	/**
	 * 到件市
	 * */
	private String tcustCity;
	
	/**
	 * 到件县
	 * */
	private String tcustCounty;
	
	/**
	 * 寄件重量
	 * */
	private Double weight;
	
	/**
	 * 物品体积
	 * */
	private String volume;
	
	/**
	 * 小写金额，代收货款和收件人付费不保留小数点；标准快递和经济快递保留两位小数点
	 * */
	private Double fee;
	
	/**
	 * 大写金额（代收货款和收件人付费需要填写）
	 * */
	private String feeUppercase;
	
	/**
	 * 付费类型,1-现金(支票)，2-记欠，3-托收，4-转帐，9-其他
	 * */
	private String payMode;
	
	/**
	 * 业务类型，1为标准快递，2为代收货款
	 * */
	private String businessType;
	
	/**
	 * 备注
	 * */
	private String remark;
	
	/**
	 * 预留字段1
	 * */
	private String blank1;
	
	/**
	 * 预留字段2
	 * */
	private String blank2;
	
	/**
	 * 预留字段3
	 * */
	private String blank3;
	
	/**
	 * 预留字段4
	 * */
	private String blank4;
	
	/**
	 * 预留字段5
	 * */
	private String blank5;
	
	/**
	 * //一票多单计费方式(1：集中主单计费 2：平均重量计费 3：分单免首重4：主分单单独计费)传数字
	 * */
	private String mainSubPayMode;
	

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public String getPackageBarcode() {
		return packageBarcode;
	}

	public void setPackageBarcode(String packageBarcode) {
		this.packageBarcode = packageBarcode;
	}


	public Integer getPackageNum() {
		return packageNum;
	}

	public void setPackageNum(Integer packageNum) {
		this.packageNum = packageNum;
	}

	public String getScontactor() {
		return scontactor;
	}

	public void setScontactor(String scontactor) {
		this.scontactor = scontactor;
	}

	public String getScustMobile() {
		return scustMobile;
	}

	public void setScustMobile(String scustMobile) {
		this.scustMobile = scustMobile;
	}

	public String getScustTelplus() {
		return scustTelplus;
	}

	public void setScustTelplus(String scustTelplus) {
		this.scustTelplus = scustTelplus;
	}

	public String getScustPost() {
		return scustPost;
	}

	public void setScustPost(String scustPost) {
		this.scustPost = scustPost;
	}

	public String getScustAddr() {
		return scustAddr;
	}

	public void setScustAddr(String scustAddr) {
		this.scustAddr = scustAddr;
	}

	public String getScustComp() {
		return scustComp;
	}

	public void setScustComp(String scustComp) {
		this.scustComp = scustComp;
	}

	public String getTcontactor() {
		return tcontactor;
	}

	public void setTcontactor(String tcontactor) {
		this.tcontactor = tcontactor;
	}

	public String getTcustMobile() {
		return tcustMobile;
	}

	public void setTcustMobile(String tcustMobile) {
		this.tcustMobile = tcustMobile;
	}

	public String getTcustTelplus() {
		return tcustTelplus;
	}

	public void setTcustTelplus(String tcustTelplus) {
		this.tcustTelplus = tcustTelplus;
	}

	public String getTcustPost() {
		return tcustPost;
	}

	public void setTcustPost(String tcustPost) {
		this.tcustPost = tcustPost;
	}

	public String getTcustAddr() {
		return tcustAddr;
	}

	public void setTcustAddr(String tcustAddr) {
		this.tcustAddr = tcustAddr;
	}

	public String getTcustComp() {
		return tcustComp;
	}

	public void setTcustComp(String tcustComp) {
		this.tcustComp = tcustComp;
	}

	public String getTcustProvince() {
		return tcustProvince;
	}

	public void setTcustProvince(String tcustProvince) {
		this.tcustProvince = tcustProvince;
	}

	public String getTcustCity() {
		return tcustCity;
	}

	public void setTcustCity(String tcustCity) {
		this.tcustCity = tcustCity;
	}

	public String getTcustCounty() {
		return tcustCounty;
	}

	public void setTcustCounty(String tcustCounty) {
		this.tcustCounty = tcustCounty;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

	public String getFeeUppercase() {
		return feeUppercase;
	}

	public void setFeeUppercase(String feeUppercase) {
		this.feeUppercase = feeUppercase;
	}

	public String getPayMode() {
		return payMode;
	}

	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getBlank1() {
		return blank1;
	}

	public void setBlank1(String blank1) {
		this.blank1 = blank1;
	}

	public String getBlank2() {
		return blank2;
	}

	public void setBlank2(String blank2) {
		this.blank2 = blank2;
	}

	public String getBlank3() {
		return blank3;
	}

	public void setBlank3(String blank3) {
		this.blank3 = blank3;
	}

	public String getBlank4() {
		return blank4;
	}

	public void setBlank4(String blank4) {
		this.blank4 = blank4;
	}

	public String getBlank5() {
		return blank5;
	}

	public void setBlank5(String blank5) {
		this.blank5 = blank5;
	}

	public String getMainSubPayMode() {
		return mainSubPayMode;
	}

	public void setMainSubPayMode(String mainSubPayMode) {
		this.mainSubPayMode = mainSubPayMode;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getSysAccount() {
		return sysAccount;
	}

	public void setSysAccount(String sysAccount) {
		this.sysAccount = sysAccount;
	}

}
