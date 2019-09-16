package com.jd.bluedragon.common.dto.basedata.response;

import java.io.Serializable;

/**
 * @author : xumigen
 * @date : 2019/9/10
 */
public class BaseDataDictDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID值
     */
    private Integer id;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 类型编码
     */
    private Integer typeCode;

    /**
     * 类型分组
     */
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
