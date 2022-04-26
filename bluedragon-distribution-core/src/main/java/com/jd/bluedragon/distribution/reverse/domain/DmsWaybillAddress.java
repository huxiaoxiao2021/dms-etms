package com.jd.bluedragon.distribution.reverse.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class DmsWaybillAddress implements Serializable {
	private static final long serialVersionUID = 2462618889876478046L;
	private Integer provinceId;
	private String provinceName;
	private Integer cityId;
	private String cityName;
	private Integer countryId;
	private String countryName;
	private Integer countrysideId;
	private String countrysideName;
	private String address;
	private String contact;
	private String phone;
	private String mobile;
	private String companyName;
	private String credentialsNumber;
	private Integer credentialsType;
	private BigDecimal longitude;
	private BigDecimal latitude;
	private String coordinateType;

	public Integer getProvinceId() {
		return this.provinceId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}

	public String getProvinceName() {
		return this.provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public Integer getCityId() {
		return this.cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return this.cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public Integer getCountryId() {
		return this.countryId;
	}

	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	public String getCountryName() {
		return this.countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public Integer getCountrysideId() {
		return this.countrysideId;
	}

	public void setCountrysideId(Integer countrysideId) {
		this.countrysideId = countrysideId;
	}

	public String getCountrysideName() {
		return this.countrysideName;
	}

	public void setCountrysideName(String countrysideName) {
		this.countrysideName = countrysideName;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getCompanyName() {
		return this.companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCredentialsNumber() {
		return this.credentialsNumber;
	}

	public void setCredentialsNumber(String credentialsNumber) {
		this.credentialsNumber = credentialsNumber;
	}

	public Integer getCredentialsType() {
		return this.credentialsType;
	}

	public void setCredentialsType(Integer credentialsType) {
		this.credentialsType = credentialsType;
	}

	public BigDecimal getLongitude() {
		return this.longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public BigDecimal getLatitude() {
		return this.latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public String getCoordinateType() {
		return this.coordinateType;
	}

	public void setCoordinateType(String coordinateType) {
		this.coordinateType = coordinateType;
	}
}