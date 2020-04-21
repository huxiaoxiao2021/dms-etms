package com.jd.bluedragon.distribution.coldchain.dto;

/**
 * @author lijie
 * @date 2020/4/3 16:16
 */
public class ThermometerRequest {

    /**
     * 温度计号
     */
    private String thermometerCode;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 保温箱号
     */
    private String cabinetCode;

    public String getThermometerCode() {
        return thermometerCode;
    }

    public void setThermometerCode(String thermometerCode) {
        this.thermometerCode = thermometerCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getCabinetCode() {
        return cabinetCode;
    }

    public void setCabinetCode(String cabinetCode) {
        this.cabinetCode = cabinetCode;
    }
}
