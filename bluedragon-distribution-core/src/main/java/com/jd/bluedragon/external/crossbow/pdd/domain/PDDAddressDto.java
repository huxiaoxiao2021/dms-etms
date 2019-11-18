package com.jd.bluedragon.external.crossbow.pdd.domain;

/**
 * <p>
 *     拼多多的地址对象
 *
 * @author wuzuxiang
 * @since 2019/10/14
 **/
public class PDDAddressDto {

    /**
     * 省份（必须）
     */
    private String province;

    /**
     * 城市（非必须）
     */
    private String city;

    /**
     * 地区（非必须）
     */
    private String area;

    /**
     * 街道/镇（非必须）
     */
    private String town;

    /**
     * 剩余详细地址（必须）
     */
    private String detail;

    /**
     * 邮编（非必须）
     */
    private String zip;

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

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
