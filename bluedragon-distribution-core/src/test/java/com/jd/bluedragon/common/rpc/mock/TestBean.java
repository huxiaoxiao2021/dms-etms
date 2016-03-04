package com.jd.bluedragon.common.rpc.mock;

public class TestBean {
	
	
	private String username;
	
	private int age;
	
	private long longValue;
	
	/** 重量 */
	private Double weight;

	/** 数量 */
	private Integer quantity;

	/** 地址 */
	private String address;

	/** 是否打印包裹 */
	private int isPrintPack;

	/** 是否打印发票 */
	private int isPrintInvoice;

	/** 机构ID */
	private Integer orgId;

	/** 库房ID */
	private Integer storeId;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public long getLongValue() {
		return longValue;
	}

	public void setLongValue(long longValue) {
		this.longValue = longValue;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getIsPrintPack() {
		return isPrintPack;
	}

	public void setIsPrintPack(int isPrintPack) {
		this.isPrintPack = isPrintPack;
	}

	public int getIsPrintInvoice() {
		return isPrintInvoice;
	}

	public void setIsPrintInvoice(int isPrintInvoice) {
		this.isPrintInvoice = isPrintInvoice;
	}

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	@Override
	public String toString() {
		return "TestBean [address=" + address + ", age=" + age
				+ ", isPrintInvoice=" + isPrintInvoice + ", isPrintPack="
				+ isPrintPack + ", longValue=" + longValue + ", orgId=" + orgId
				+ ", quantity=" + quantity + ", storeId=" + storeId
				+ ", username=" + username + ", weight=" + weight + "]";
	}
	

}
