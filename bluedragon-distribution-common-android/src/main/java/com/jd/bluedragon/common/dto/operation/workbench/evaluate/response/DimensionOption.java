package com.jd.bluedragon.common.dto.operation.workbench.evaluate.response;

import java.io.Serializable;

/**
 * 评价维度枚举选项
 */
public class DimensionOption implements Serializable {

    private static final long serialVersionUID = -8897952395688640193L;

    /**
     * 选项值
     */
    private Integer code;

    /**
     * 描述
     */
    private String name;

    /**
     * 详细描述
     */
    private String desc;

    /**
     * 选项状态：1-特殊关注
     */
    private Integer status;

    /**
     * 是否有文本框：0-无，1-有
     */
    private Integer hasTextBox;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getHasTextBox() {
        return hasTextBox;
    }

    public void setHasTextBox(Integer hasTextBox) {
        this.hasTextBox = hasTextBox;
    }
}
