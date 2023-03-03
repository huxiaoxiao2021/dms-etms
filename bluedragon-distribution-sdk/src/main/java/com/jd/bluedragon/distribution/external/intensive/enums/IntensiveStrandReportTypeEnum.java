package com.jd.bluedragon.distribution.external.intensive.enums;

/**
 * 集约-滞留上报扫描条码类型
 *
 * @author hujiping
 * @date 2023/3/8 5:40 PM
 */
public enum IntensiveStrandReportTypeEnum {
    
    PACKAGE_CODE(1,"包裹号"),
    
    WAYBILL_CODE(2, "运单号");

    private Integer code;

    private String desc;
    
    IntensiveStrandReportTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static String getReportTypeName(int code){
        for(IntensiveStrandReportTypeEnum reportTypeEnum : IntensiveStrandReportTypeEnum.values()){
            if(reportTypeEnum.getCode() == code){
                return reportTypeEnum.getDesc();
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
