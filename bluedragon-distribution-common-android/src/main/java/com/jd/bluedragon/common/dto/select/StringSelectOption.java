package com.jd.bluedragon.common.dto.select;

import java.io.Serializable;

/**
 * @ClassName StringSelectOption
 * @Description
 * @Author wyh
 * @Date 2022/4/2 14:30
 **/
public class StringSelectOption implements Serializable {

    /**
     * 选项值
     */
    private String code;

    /**
     * 描述
     */
    private String name;

    /**
     * 排序
     */
    private Integer order;

    public StringSelectOption() {}

    public StringSelectOption(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public StringSelectOption(String code, String name, Integer order) {
        this.code = code;
        this.name = name;
        this.order = order;
    }

    public static class OrderComparator implements java.util.Comparator<StringSelectOption> {
        @Override
        public int compare(StringSelectOption o1, StringSelectOption o2) {
            return o1.getOrder().compareTo(o2.getOrder());
        }
    }

    public static class CodeComparator implements java.util.Comparator<StringSelectOption> {
        @Override
        public int compare(StringSelectOption o1, StringSelectOption o2) {
            return o1.getCode().compareTo(o2.getCode());
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
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
}
