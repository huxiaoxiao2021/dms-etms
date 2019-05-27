package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

/**
 * @author : xumigen
 * @date : 2019/5/9
 */
public class PackageSummaryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
