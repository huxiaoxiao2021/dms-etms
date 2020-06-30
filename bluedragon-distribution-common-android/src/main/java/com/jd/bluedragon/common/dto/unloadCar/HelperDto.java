package com.jd.bluedragon.common.dto.unloadCar;

import java.io.Serializable;

/**
 * @ClassName: HelperDto
 * @Description: 类作用描述
 * @Author: biyubo
 * @CreateDate: 2020-6-22 17:08
 */

public class HelperDto implements Serializable {
    private static final long serialVersionUID = -1L;

    private String helperERP;
    private String helperName;

    public String getHelperERP() {
        return helperERP;
    }

    public void setHelperERP(String helperERP) {
        this.helperERP = helperERP;
    }

    public String getHelperName() {
        return helperName;
    }

    public void setHelperName(String helperName) {
        this.helperName = helperName;
    }
}