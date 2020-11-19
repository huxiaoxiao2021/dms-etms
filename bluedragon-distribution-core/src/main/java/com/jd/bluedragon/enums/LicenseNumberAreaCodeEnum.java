package com.jd.bluedragon.enums;

import org.apache.commons.lang.StringUtils;

/**
 * @program: bluedragon-distribution
 * @description: 车牌区域
 * @author: wuming
 * @create: 2020-10-15 22:03
 */
public enum LicenseNumberAreaCodeEnum {
    SHANGHAI("021", "沪"),
    TIANJIN("022", "津"),
    BEIJING("010", "京"),
    CHONGQING("023", "渝"),
    FUJIAN("0591", "闽"),
    GANSU("0931", "甘"),
    GUANGDONG("020", "粤"),
    GUANGXI("0771", "桂"),
    GUIZHOU("0851", "贵"),
    HEBEI("0311", "冀"),
    HEILONGJIANG("0451", "黑"),
    HENAN("0371", "豫"),
    HUBEI("027", "鄂"),
    HUNAN("0731", "湘"),
    JIANGSU("025", "苏"),
    JIANGXI("0791", "赣"),
    JILIN("0431", "吉"),
    LIAONING("024", "辽"),
    NEIMENGGU("0471", "蒙"),
    NINGXIA("0951", "宁"),
    SHANDONG("0531", "鲁"),
    SHANXI("0351", "晋"),
    SHAN_XI("029", "陕"),
    SICHUAN("028", "川"),
    XINJIANG("0991", "新"),
    XIZANG("0891", "藏"),
    YUNAN("0871", "云"),
    ZHEJIANG("0571", "浙"),
    QINGHAI("0971", "青"),
    HAINAN("0898", "琼"),
    ANHUI("0551", "皖"),
    ;


    private String areaCode;
    private String areaName;

    LicenseNumberAreaCodeEnum(String areaCode, String areaName) {
        this.areaCode = areaCode;
        this.areaName = areaName;
    }


    public String getAreaCode() {
        return areaCode;
    }

    public String getAreaName() {
        return areaName;
    }


    /**
     * 转换车牌号 (ex: 010A00001->京A00001)
     *
     * @param licenseNumber
     * @return
     */
    public static String transferLicenseNumber(String licenseNumber) {
        String str = licenseNumber.substring(0, 3);
        for (LicenseNumberAreaCodeEnum licenseNumberAreaCodeEnum : LicenseNumberAreaCodeEnum.values()) {
            if (licenseNumberAreaCodeEnum.getAreaCode().equals(str)) {
                str = licenseNumberAreaCodeEnum.getAreaName();
                break;
            }
        }
        StringBuilder stringBuilder = new StringBuilder(str);
        stringBuilder.append(StringUtils.substring(licenseNumber, 3, 9));
        return stringBuilder.toString();
    }

    /**
     * 转换车牌号 (ex: 0971A00001->川A00001)
     *
     * @param licenseNumber
     * @return
     */
    public static String transferLicenseNumber2(String licenseNumber) {
        String str = licenseNumber.substring(0, 4);
        for (LicenseNumberAreaCodeEnum licenseNumberAreaCodeEnum : LicenseNumberAreaCodeEnum.values()) {
            if (licenseNumberAreaCodeEnum.getAreaCode().equals(str)) {
                str = licenseNumberAreaCodeEnum.getAreaName();
                break;
            }
        }
        StringBuilder stringBuilder = new StringBuilder(str);
        stringBuilder.append(StringUtils.substring(licenseNumber, 4, 10));
        return stringBuilder.toString();
    }

}
