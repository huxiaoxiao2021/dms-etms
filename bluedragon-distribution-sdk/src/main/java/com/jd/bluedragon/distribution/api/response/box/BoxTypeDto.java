package com.jd.bluedragon.distribution.api.response.box;

import java.io.Serializable;

/**
 * 箱号类型
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-10-24 09:27:16 周二
 */
public class BoxTypeDto implements Serializable {
    private static final long serialVersionUID = -4165001624177808402L;

    /**
     * 选项值
     */
    private String typeCode;

    /**
     * 子类型值
     */
    private String subTypeCode;

    /**
     * 描述
     */
    private String name;

    /**
     * 详细描述
     */
    private String desc;

    /**
     * 排序
     */
    private Integer order;

    public BoxTypeDto() {
    }

    public String getTypeCode() {
        return typeCode;
    }

    public BoxTypeDto setTypeCode(String typeCode) {
        this.typeCode = typeCode;
        return this;
    }

    public String getSubTypeCode() {
        return subTypeCode;
    }

    public BoxTypeDto setSubTypeCode(String subTypeCode) {
        this.subTypeCode = subTypeCode;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
