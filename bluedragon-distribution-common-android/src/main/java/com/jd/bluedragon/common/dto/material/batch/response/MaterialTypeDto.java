package com.jd.bluedragon.common.dto.material.batch.response;

import java.io.Serializable;

/**
 * @ClassName MaterialTypeDto
 * @Description
 * @Author wyh
 * @Date 2020/3/24 10:19
 **/
public class MaterialTypeDto implements Serializable {

    private static final long serialVersionUID = -6625099023040695112L;

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
