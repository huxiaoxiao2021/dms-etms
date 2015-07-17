package com.jd.bluedragon.distribution.send.domain.reverse;

/**
 * 订单验货明细Bean
 * className CheckBoxOrderDetail
 * @author houxiaofang
 * @create 2011-8-4
 */
public class CheckBoxOrderDetail {
//id
private int id;
//订单号
private String orderNo;
//状态  0 部分验货  1 部分拒收  2 整单拒收 3 全部通过
private int status;
//回传状态 0.未回传 1.已回传
private int eosStatus;
/**
 * @return the id
 */
public int getId() {
	return id;
}
/**
 * @param id the id to set
 */
public void setId(int id) {
	this.id = id;
}
/**
 * @return the orderNo
 */
public String getOrderNo() {
	return orderNo;
}
/**
 * @param orderNo the orderNo to set
 */
public void setOrderNo(String orderNo) {
	this.orderNo = orderNo;
}
/**
 * @return the status
 */
public int getStatus() {
	return status;
}
/**
 * @param status the status to set
 */
public void setStatus(int status) {
	this.status = status;
}
/**
 * @return the eosStatus
 */
public int getEosStatus() {
	return eosStatus;
}
/**
 * @param eosStatus the eosStatus to set
 */
public void setEosStatus(int eosStatus) {
	this.eosStatus = eosStatus;
}

}
