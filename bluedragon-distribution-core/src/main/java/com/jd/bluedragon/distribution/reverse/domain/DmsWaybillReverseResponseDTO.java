package com.jd.bluedragon.distribution.reverse.domain;

import java.io.Serializable;
import java.util.List;
/**
 * 分拣对象DmsWaybillReverseResponseDTO
 * @author wuyoude
 *
 */
public class DmsWaybillReverseResponseDTO implements Serializable {
	private static final long serialVersionUID = 2424923857771253346L;
	
	private String waybillCode;
	private String senderName;
	private String senderAddress;
	private String senderTel;
	private String senderMobile;
	private Integer provinceId;
	private String receiveName;
	private String receiveAddress;
	private String receiveTel;
	private String receiveMobile;
	private String province;
	private Integer cityId;
	private String city;
	private Integer countyId;
	private String county;
	private Integer townId;
	private String town;
	private Integer packageCount;
	private List<DmsPackageDTO> packageDTOList;

	public String getWaybillCode() {
		return this.waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public String getSenderName() {
		return this.senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderAddress() {
		return this.senderAddress;
	}

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	public String getSenderTel() {
		return this.senderTel;
	}

	public void setSenderTel(String senderTel) {
		this.senderTel = senderTel;
	}

	public String getSenderMobile() {
		return this.senderMobile;
	}

	public void setSenderMobile(String senderMobile) {
		this.senderMobile = senderMobile;
	}

	public Integer getProvinceId() {
		return this.provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}

	public String getReceiveName() {
		return this.receiveName;
	}

	public void setReceiveName(String receiveName) {
		this.receiveName = receiveName;
	}

	public String getReceiveAddress() {
		return this.receiveAddress;
	}

	public void setReceiveAddress(String receiveAddress) {
		this.receiveAddress = receiveAddress;
	}

	public String getReceiveTel() {
		return this.receiveTel;
	}

	public void setReceiveTel(String receiveTel) {
		this.receiveTel = receiveTel;
	}

	public String getReceiveMobile() {
		return this.receiveMobile;
	}

	public void setReceiveMobile(String receiveMobile) {
		this.receiveMobile = receiveMobile;
	}

	public String getProvince() {
		return this.province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public Integer getCityId() {
		return this.cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Integer getCountyId() {
		return this.countyId;
	}

	public void setCountyId(Integer countyId) {
		this.countyId = countyId;
	}

	public String getCounty() {
		return this.county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public Integer getTownId() {
		return this.townId;
	}

	public void setTownId(Integer townId) {
		this.townId = townId;
	}

	public String getTown() {
		return this.town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public Integer getPackageCount() {
		return this.packageCount;
	}

	public void setPackageCount(Integer packageCount) {
		this.packageCount = packageCount;
	}

	public List<DmsPackageDTO> getPackageDTOList() {
		return this.packageDTOList;
	}

	public void setPackageDTOList(List<DmsPackageDTO> packageDTOList) {
		this.packageDTOList = packageDTOList;
	}
}