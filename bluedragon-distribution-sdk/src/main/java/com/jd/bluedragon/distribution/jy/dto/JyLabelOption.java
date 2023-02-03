package com.jd.bluedragon.distribution.jy.dto;

import java.io.Serializable;

/**
 * @ClassName LabelOption
 * @Description 标签
 * @Author wyh
 * @Date 2022/4/9 17:46
 **/
public class JyLabelOption implements Serializable {

    private static final long serialVersionUID = 7165882444785280074L;

    /**
     * 选项值
     */
    private Integer code;

    /**
     * 描述
     */
    private String name;

    /**
     * 排序
     */
    private Integer order;

    /**
     * 是否展示。默认展示
     */
    private Boolean display = true;

    public JyLabelOption() {}

    public JyLabelOption(Integer code, String name) {
        this.init(code, name, null, null);
    }

    public JyLabelOption(Integer code, String name, Integer order) {
        this.init(code, name, order, null);
    }

    public JyLabelOption(Integer code, String name, Integer order, Boolean display) {
        this.init(code, name, order, display);
    }

    private void init(Integer code, String name, Integer order, Boolean display) {
        this.code = code;
        this.name = name;
        this.order = order;
        this.display = display;
    }

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

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Boolean getDisplay() {
        return display;
    }

    public void setDisplay(Boolean display) {
        this.display = display;
    }
}
