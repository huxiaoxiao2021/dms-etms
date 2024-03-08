package com.jd.bluedragon.distribution.box.domain;

import java.util.List;

public class Container {
    /**
     * 类型
     */
    private Integer type;
    /**
     * 编号
     */
    private String code;

    /**
     * 子容器
     */
    private List<Container> children;

    /**
     * 层级
     */
    private Integer level;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Container> getChildren() {
        return children;
    }

    public void setChildren(List<Container> children) {
        this.children = children;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
