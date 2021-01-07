package com.jd.bluedragon.common.dto.exceptionReport.expressBill.Enum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public enum ExpressBillExceptionReportTypeEnum implements Serializable {

    EXPRESS_REPORT_MAX_SPACE(1,"面单未贴箱体最大面"),
    EXPRESS_REPORT_FOLD(2,"双面单"),
    EXPRESS_REPORT_CREVICE(3,"面单骑缝"),
    EXPRESS_REPORT_OTHER(4,"其他");

    ExpressBillExceptionReportTypeEnum(Integer reportTypeCode, String reportName) {
        this.reportTypeCode = reportTypeCode;
        this.reportName = reportName;
    }

    /**
     * 举报类型编码
     */
    private Integer reportTypeCode;

    /**
     * 举报类型名称
     */
    private String reportName;

    /**
     * 获取所有枚举值
     * @return
     */
    public static List<ExpressBillExceptionReportTypeEnum>  getAllExpressBillExceptionReportType(){
        List<ExpressBillExceptionReportTypeEnum>  list = new ArrayList<ExpressBillExceptionReportTypeEnum>();
        ExpressBillExceptionReportTypeEnum[]  allType =  values();
        for(ExpressBillExceptionReportTypeEnum expressBillExceptionReportTypeEnum:allType){
            list.add(expressBillExceptionReportTypeEnum);
        }
        return  list;
    }

    public Integer getReportTypeCode() {
        return reportTypeCode;
    }

    public void setReportTypeCode(Integer reportTypeCode) {
        this.reportTypeCode = reportTypeCode;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    @Override
    public String toString() {
        return  reportName;
    }
}
