package com.jd.bluedragon.distribution.jy.dto.unload;


import java.io.Serializable;

public class QueryGoodsCategory extends UnloadBaseDto implements Serializable {
    private static final long serialVersionUID = -3384319549017831122L;
    /**
     * 任务或者板号
     */
    private String barCode;
    /**
     * 1 按任务 2按板
     */
    private Integer type;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
