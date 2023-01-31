package com.jd.bluedragon.distribution.jy.dto;

import java.io.Serializable;

/**
 * @ClassName SelectOption
 * @Description 通用的下拉框数据源
 * @Author wyh
 * @Date 2022/3/9 17:43
 **/
public class JySelectOption implements Serializable {

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
     * 排序
     */
    private Integer order;

    public JySelectOption() {}

    public JySelectOption(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public JySelectOption(Integer code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public JySelectOption(Integer code, String name, Integer order) {
        this.code = code;
        this.name = name;
        this.order = order;
    }

    public JySelectOption(Integer code, String name, String desc, Integer order) {
        this.code = code;
        this.name = name;
        this.desc = desc;
        this.order = order;
    }

    public static class OrderComparator implements java.util.Comparator<JySelectOption> {
        @Override
        public int compare(JySelectOption o1, JySelectOption o2) {
            return o1.getOrder().compareTo(o2.getOrder());
        }
    }

    public static class CodeComparator implements java.util.Comparator<JySelectOption> {
        @Override
        public int compare(JySelectOption o1, JySelectOption o2) {
            return o1.getCode().compareTo(o2.getCode());
        }
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
