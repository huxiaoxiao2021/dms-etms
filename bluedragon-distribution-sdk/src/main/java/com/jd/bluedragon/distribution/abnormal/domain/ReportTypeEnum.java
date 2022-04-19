package com.jd.bluedragon.distribution.abnormal.domain;

/**
 * 上报条码类型枚举
 * @author jinjingcheng
 * @date 2020/3/10
 */
public enum ReportTypeEnum {

    PACKAGE_CODE(1,"包裹号"),
    WAYBILL_CODE(2, "运单号"),
    BOX_CODE(3, "箱号"),
    BATCH_NO(4, "批次号"),
    BOARD_NO(5, "板号");

    private Integer code;

    private String desc;

     ReportTypeEnum() {
    }

     ReportTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static String getReportTypeName(int code){
         for(ReportTypeEnum reportTypeEnum : ReportTypeEnum.values()){
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
