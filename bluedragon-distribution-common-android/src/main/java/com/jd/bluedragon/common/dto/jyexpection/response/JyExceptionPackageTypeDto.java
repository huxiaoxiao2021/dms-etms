package com.jd.bluedragon.common.dto.jyexpection.response;

import java.io.Serializable;
import java.util.List;

public class JyExceptionPackageTypeDto implements Serializable {

    /**
     * 类型code 值
     */
    private Integer code;

    /**
     * 类型名称
     */
    private String name;


    private Boolean disabled = false;

    /**
     * 子类型
     */
    List<JyExceptionPackageTypeDto> children;

    public JyExceptionPackageTypeDto(){}
    public JyExceptionPackageTypeDto(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public JyExceptionPackageTypeDto(Integer code, String name, List<JyExceptionPackageTypeDto> children) {
        this.code = code;
        this.name = name;
        this.children = children;
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

    public List<JyExceptionPackageTypeDto> getChildren() {
        return children;
    }

    public void setChildren(List<JyExceptionPackageTypeDto> children) {
        this.children = children;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }
}
