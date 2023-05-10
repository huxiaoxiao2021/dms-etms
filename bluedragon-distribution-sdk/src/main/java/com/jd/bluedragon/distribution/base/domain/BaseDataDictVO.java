package com.jd.bluedragon.distribution.base.domain;

import java.io.Serializable;

/**
 * 基础配置DTO
 *
 * @author hujiping
 * @date 2023/3/8 3:30 PM
 */
public class BaseDataDictVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID值
     */
    private Integer id;
    private String typeName;
    private Integer typeCode;
    private Integer typeGroup;

    /**
     * 父节点ID
     */
    private Integer parentId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(Integer typeCode) {
        this.typeCode = typeCode;
    }

    public Integer getTypeGroup() {
        return typeGroup;
    }

    public void setTypeGroup(Integer typeGroup) {
        this.typeGroup = typeGroup;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

}
