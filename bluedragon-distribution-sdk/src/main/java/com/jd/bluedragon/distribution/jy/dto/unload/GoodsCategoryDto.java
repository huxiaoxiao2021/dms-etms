package com.jd.bluedragon.distribution.jy.dto.unload;


import java.io.Serializable;
public class GoodsCategoryDto implements Serializable {
    private static final long serialVersionUID = 7431307375891856971L;
    /**
     * 货物类型： UnloadProductTypeEnum
     */
    private String  type;
    /**
     *名称： UnloadProductTypeEnum
     */
    private String name;
    /**
     * 货物计数
     */
    private Integer count;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
