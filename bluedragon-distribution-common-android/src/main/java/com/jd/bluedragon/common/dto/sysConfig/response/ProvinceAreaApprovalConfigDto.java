package com.jd.bluedragon.common.dto.sysConfig.response;

import java.io.Serializable;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/11/7 14:45
 * @Description: 省区审核人配置
 */
public class ProvinceAreaApprovalConfigDto implements Serializable {
    private static final long serialVersionUID = 1821261252671680786L;

    /**
     * 省区编码
     */
    private String provinceAreaCode;

    /**
     * 第一审核人erp
     */
    private String firstChecker;

    /**
     *  第二审核人erp
     */
    private String secondChecker;

    public String getProvinceAreaCode() {
        return provinceAreaCode;
    }

    public void setProvinceAreaCode(String provinceAreaCode) {
        this.provinceAreaCode = provinceAreaCode;
    }

    public String getFirstChecker() {
        return firstChecker;
    }

    public void setFirstChecker(String firstChecker) {
        this.firstChecker = firstChecker;
    }

    public String getSecondChecker() {
        return secondChecker;
    }

    public void setSecondChecker(String secondChecker) {
        this.secondChecker = secondChecker;
    }
}
