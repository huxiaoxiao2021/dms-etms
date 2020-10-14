package com.jd.bluedragon.distribution.reverse.domain;

import java.io.Serializable;
/**
 * 退货地址扩展信息
 * @author wuyoude
 *
 */
public class BackAddressDTOExt implements Serializable{

	private static final long serialVersionUID = 1L;
	/**
	 * 退货地址
	 */
	private String backAddress;
	/**
	 * 退货全地址（包含省市县）
	 */
	private String fullBackAddress;
	/**
	 * 联系人
	 */
	private String contractName;
	/**
	 * 手机
	 */
	private String contractPhone;
	/**
	 * 电话
	 */
	private String contractMobile;
	public String getBackAddress() {
		return backAddress;
	}
	public void setBackAddress(String backAddress) {
		this.backAddress = backAddress;
	}
	public String getFullBackAddress() {
		return fullBackAddress;
	}
	public void setFullBackAddress(String fullBackAddress) {
		this.fullBackAddress = fullBackAddress;
	}
	public String getContractName() {
		return contractName;
	}
	public void setContractName(String contractName) {
		this.contractName = contractName;
	}
	public String getContractPhone() {
		return contractPhone;
	}
	public void setContractPhone(String contractPhone) {
		this.contractPhone = contractPhone;
	}
	public String getContractMobile() {
		return contractMobile;
	}
	public void setContractMobile(String contractMobile) {
		this.contractMobile = contractMobile;
	}
}
