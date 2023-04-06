package com.jd.bluedragon.core.jsf.address;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/3/1 11:21
 * @Description: 四级地址信息
 */
public class DmsExternalJDAddressResponse implements Serializable {

    private static final long serialVersionUID = 1014811169302293229L;

    private Integer provinceCode;
    private String provinceName;
    private Integer cityCode;
    private String cityName;
    private Integer districtCode;
    private String districtName;
    private Integer townCode;
    private String townName;
    private String detailAddress;
    private String fullAddress;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer precise;
    private String orderId;
    private Long adminCode;
    private Integer wrongLevel;

    public Integer getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(Integer provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public Integer getCityCode() {
        return cityCode;
    }

    public void setCityCode(Integer cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(Integer districtCode) {
        this.districtCode = districtCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public Integer getTownCode() {
        return townCode;
    }

    public void setTownCode(Integer townCode) {
        this.townCode = townCode;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Integer getPrecise() {
        return precise;
    }

    public void setPrecise(Integer precise) {
        this.precise = precise;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getAdminCode() {
        return adminCode;
    }

    public void setAdminCode(Long adminCode) {
        this.adminCode = adminCode;
    }

    public Integer getWrongLevel() {
        return wrongLevel;
    }

    public void setWrongLevel(Integer wrongLevel) {
        this.wrongLevel = wrongLevel;
    }
}
