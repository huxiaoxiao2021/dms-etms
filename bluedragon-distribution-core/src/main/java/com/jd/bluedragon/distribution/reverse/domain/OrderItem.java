package com.jd.bluedragon.distribution.reverse.domain;

public class OrderItem {

	/**
	 * 商品编号
	 */
	public String itemId;
	/**
	 * 商品名称
	 */
	public String itemName;
	/**
	 * 京东价格
	 */
	public String itemPrice;
	/**
	 * 商品数量
	 */
	public OrderItem(){
		
	}
	public int itemNum=0;
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getItemPrice() {
		return itemPrice;
	}
	public void setItemPrice(String itemPrice) {
		this.itemPrice = itemPrice;
	}
	public int getItemNum() {
		return itemNum;
	}
	public void setItemNum(int itemNum) {
		this.itemNum = itemNum;
	}
	
	

}
