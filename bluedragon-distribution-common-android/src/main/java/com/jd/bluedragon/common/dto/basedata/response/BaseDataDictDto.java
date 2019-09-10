package com.jd.bluedragon.common.dto.basedata.response;

import java.io.Serializable;

/**
 * @author : xumigen
 * @date : 2019/9/10
 */
public class BaseDataDictDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String typeName;
    private Integer typeCode;
    private Integer typeGroup;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTypeGroup() {
        return typeGroup;
    }

    public void setTypeGroup(Integer typeGroup) {
        this.typeGroup = typeGroup;
    }
}
