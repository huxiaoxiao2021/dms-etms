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
    FUJIAN("591", "闽"),
    GANSU("931", "甘"),
    GUANGDONG("020", "粤"),
    GUANGXI("771", "桂"),
    GUIZHOU("851", "贵"),
    HEBEI("311", "冀"),
    HEILONGJIANG("451", "黑"),
    HENAN("371", "豫"),
    HUBEI("027", "鄂"),
    HUNAN("731", "湘"),
    JIANGSU("025", "苏"),
    JIANGXI("791", "赣"),
    JILIN("431", "吉"),
    LIAONING("024", "辽"),
    NEIMENGGU("471", "蒙"),
    NINGXIA("951", "宁"),
    SHANDONG("531", "鲁"),
    SHANXI("351", "晋"),
    SHAN_XI("029", "陕"),
    SICHUAN("028", "川"),
    XINJIANG("991", "新"),
    XIZANG("891", "藏"),
    YUNAN("871", "云"),
    ZHEJIANG("571", "浙"),
    QINGHAI("971", "青"),
    HAINAN("898", "琼"),
    ANHUI("551", "皖"),
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
        String substring = StringUtils.substring(licenseNumber, 3, 9);
        return str + substring;
    }

}
