package com.jd.bluedragon.distribution.base.domain;

import java.io.Serializable;

/**
 * 省区VO
 *
 * @author hujiping
 * @date 2023/5/23 2:33 PM
 */
public class BaseProvinceAgencyVO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * 省区编码
     */
    private String provinceAgencyCode;

    /**
     * 省区名称
     */
    private String provinceAgencyName;

    public String getProvinceAgencyCode() {
        return provinceAgencyCode;
    }

    public void setProvinceAgencyCode(String provinceAgencyCode) {
        this.provinceAgencyCode = provinceAgencyCode;
    }

    public String getProvinceAgencyName() {
        return provinceAgencyName;
    }

    public void setProvinceAgencyName(String provinceAgencyName) {
        this.provinceAgencyName = provinceAgencyName;
    }
}
