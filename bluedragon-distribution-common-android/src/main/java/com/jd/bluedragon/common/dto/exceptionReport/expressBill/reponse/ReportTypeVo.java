package com.jd.bluedragon.common.dto.exceptionReport.expressBill.reponse;

import java.io.Serializable;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2021/1/13 17:46
 */
public class ReportTypeVo implements Serializable {
    /**
     * 举报类型名称
     */
    private String reportTypeName;
    /**
     * 举报类型编码
     */
    private Integer reportTypeCode;

    public String getReportTypeName() {
        return reportTypeName;
    }

    public void setReportTypeName(String reportTypeName) {
        this.reportTypeName = reportTypeName;
    }

    public Integer getReportTypeCode() {
        return reportTypeCode;
    }

    public void setReportTypeCode(Integer reportTypeCode) {
        this.reportTypeCode = reportTypeCode;
    }

    @Override
    public String toString() {
        return reportTypeName;
    }
}
    
