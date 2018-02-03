package com.jd.ql.dms.common.domain;

import java.io.Serializable;

/**
 * 空铁项目的基础数据字典
 * Created by xumei3 on 2017/12/28.
 *
 */
public class DictionaryInfoModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 数据编码 **/
    private int typeCode;

    /** 数据值 **/
    private String typeName;

    /** 数据类型编码 10表示始发城市信息 20表示目的城市信息  30表示车型信息 **/
    private int typeGroup;


    public DictionaryInfoModel(int typeCode, String typeName, int typeGroup){
        this.typeCode = typeCode;
        this.typeName = typeName;
        this.typeGroup = typeGroup;
    }

    public int getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(int typeCode) {
        this.typeCode = typeCode;
    }

    public int getTypeGroup() {
        return typeGroup;
    }

    public void setTypeGroup(int typeGroup) {
        this.typeGroup = typeGroup;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
