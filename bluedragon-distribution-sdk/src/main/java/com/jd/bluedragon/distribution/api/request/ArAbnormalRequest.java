package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年11月30日 15时:14分
 */
public class ArAbnormalRequest extends JdRequest {

    public static final Integer TRANSPONDTYPE10_CODE = 10;
    public static final String TRANSPONDTYPE10_DESC= "航空转陆运";
    public static final Integer TRANSPONDTYPE20_CODE = 20;
    public static final String TRANSPONDTYPE20_DESC= "航空转铁路";
    public static final Integer TRANSPONDTYPE30_CODE = 30;
    public static final String TRANSPONDTYPE30_DESC= "铁路转航空";
    public static final Integer TRANSPONDTYPE40_CODE = 40;
    public static final String TRANSPONDTYPE40_DESC= "航空转高铁";
    public static final Integer TRANSPONDTYPE50_CODE = 50;
    public static final String TRANSPONDTYPE50_DESC= "航空转普列";

    public static final Integer TRANSPONDREASON10_CODE = 10;
    public static final String TRANSPONDREASON10_DESC= "违禁品";
    public static final Integer TRANSPONDREASON20_CODE = 20;
    public static final String TRANSPONDREASON20_DESC= "航班异常";
    public static final Integer TRANSPONDREASON30_CODE = 30;
    public static final String TRANSPONDREASON30_DESC= "天气原因";
    public static final Integer TRANSPONDREASON40_CODE = 40;
    public static final String TRANSPONDREASON40_DESC= "其他";

    public static final String SEPARATOR_COMMA = " ";
    /**
     * 转发类型  10 航空转陆运 、20 航空转铁路 、30 铁路转航空
     */
    private  Integer transpondType;
    /**
     * 异常原因 10违禁品、20航班异常、30天气原因、40其他
     */
    private Integer transpondReason;

    /**
     * 包裹号、运单号、箱号、批次号
     */
    private String packageCode;

    public Integer getTranspondType() {
        return transpondType;
    }

    public void setTranspondType(Integer transpondType) {
        this.transpondType = transpondType;
    }

    public Integer getTranspondReason() {
        return transpondReason;
    }

    public void setTranspondReason(Integer transpondReason) {
        this.transpondReason = transpondReason;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }
}
