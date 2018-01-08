package com.jd.bluedragon.distribution.jsf.domain;

public class WhemsWaybillEntity {
	
	// 订单唯一号，发送和反馈对应的唯一标识 field08
		private String id;

		// 订单号 field01
		private String orderId;

		// --包裹号 field09 唯一
		private String bagId;

		// 用户姓名
		private String userName;

		// 用户邮编
		private String postalCode;

		// 用户所在省
		private String province;

		// 用户所在市
		private String city;
		// 用户所在区

		private String area;

		// 用户详细地址
		private String address;

		// 手机号码
		private String cellPhoneNumber;

		// 固定电话号码
		private String telePhoneNumber;

		// 电子邮件地址
		private String emailAddress;

		// 要求送货日期
		private String deliveryTime;
		// 重量

		private String weight;

		// 体积
		private String wbulk;

		// 是否代收货款
		private String collection;

		// 应收货款金额
		private String needFund;

		// 备注
		private String remark;
		// 包裹数量

		private String bagQuatity;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getOrderId() {
			return orderId;
		}

		public void setOrderId(String orderId) {
			this.orderId = orderId;
		}

		public String getBagId() {
			return bagId;
		}

		public void setBagId(String bagId) {
			this.bagId = bagId;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getPostalCode() {
			return postalCode;
		}

		public void setPostalCode(String postalCode) {
			this.postalCode = postalCode;
		}

		public String getProvince() {
			return province;
		}

		public void setProvince(String province) {
			this.province = province;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getArea() {
			return area;
		}

		public void setArea(String area) {
			this.area = area;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getCellPhoneNumber() {
			return cellPhoneNumber;
		}

		public void setCellPhoneNumber(String cellPhoneNumber) {
			this.cellPhoneNumber = cellPhoneNumber;
		}

		public String getTelePhoneNumber() {
			return telePhoneNumber;
		}

		public void setTelePhoneNumber(String telePhoneNumber) {
			this.telePhoneNumber = telePhoneNumber;
		}

		public String getEmailAddress() {
			return emailAddress;
		}

		public void setEmailAddress(String emailAddress) {
			this.emailAddress = emailAddress;
		}

		public String getDeliveryTime() {
			return deliveryTime;
		}

		public void setDeliveryTime(String deliveryTime) {
			this.deliveryTime = deliveryTime;
		}

		public String getWeight() {
			return weight;
		}

		public void setWeight(String weight) {
			this.weight = weight;
		}

		public String getWbulk() {
			return wbulk;
		}

		public void setWbulk(String wbulk) {
			this.wbulk = wbulk;
		}

		public String getCollection() {
			return collection;
		}

		public void setCollection(String collection) {
			this.collection = collection;
		}

		public String getNeedFund() {
			return needFund;
		}

		public void setNeedFund(String needFund) {
			this.needFund = needFund;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		public String getBagQuatity() {
			return bagQuatity;
		}

		public void setBagQuatity(String bagQuatity) {
			this.bagQuatity = bagQuatity;
		}
		
}
