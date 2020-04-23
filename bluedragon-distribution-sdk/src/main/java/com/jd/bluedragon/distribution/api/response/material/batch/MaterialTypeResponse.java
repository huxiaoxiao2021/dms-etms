package com.jd.bluedragon.distribution.api.response.material.batch;

import java.io.Serializable;

/**
 * @ClassName MaterialTypeResponse
 * @Description
 * @Author wyh
 * @Date 2020/3/18 14:39
 **/
public class MaterialTypeResponse implements Serializable {

    private static final long serialVersionUID = -8422047376082811101L;

    /**
     * 物资编号
     */
    private String materialCode;

    /**
     * 物资名称
     */
    private String materialName;

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }
}
