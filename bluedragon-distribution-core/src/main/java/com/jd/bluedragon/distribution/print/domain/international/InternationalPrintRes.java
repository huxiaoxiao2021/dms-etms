package com.jd.bluedragon.distribution.print.domain.international;

import java.io.Serializable;
import java.util.List;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2023/7/19 3:50 PM
 */
public class InternationalPrintRes implements Serializable {
    
    private Integer code;

    private String msg;

    private List<RenderResultDTO> result;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<RenderResultDTO> getResult() {
        return result;
    }

    public void setResult(List<RenderResultDTO> result) {
        this.result = result;
    }
}
