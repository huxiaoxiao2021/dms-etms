package com.jd.bluedragon.distribution.sendprint.domain;

import com.jd.dms.wb.report.api.dto.printhandover.SummaryPrintResult;

import java.io.Serializable;
import java.util.List;

/**
 * 发货交接清单-汇总查询结果集
 * @author hujiping
 */
public class SummaryPrintESResultResponse implements Serializable{

    private static final long serialVersionUID = 1L;

    private Integer code;

    private String message;
    
    private List<SummaryPrintResult> data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SummaryPrintResult> getData() {
        return data;
    }

    public void setData(List<SummaryPrintResult> data) {
        this.data = data;
    }
}
