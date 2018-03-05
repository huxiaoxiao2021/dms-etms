package com.jd.ql.dms.common.domain;

import java.io.Serializable;
/**
 * 空铁项目的基础数据字典
 * Created by xumei3 on 2017/12/28.
 *
 */
public class DictionaryInfoModel implements Serializable,Comparable<DictionaryInfoModel>{
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

    @Override
    public int compareTo(DictionaryInfoModel dictionaryInfoModel) {
        if (dictionaryInfoModel == null) {
            throw new NullPointerException();
        }
        //优先使用typeGroup排序，在typeGroup相同的情况下，使用typeCode排序
        if(this.typeGroup > dictionaryInfoModel.typeGroup){
            return 1;
        } else if (this.typeGroup < dictionaryInfoModel.typeGroup) {
            return -1;
        } else {
            if (this.typeCode > dictionaryInfoModel.typeCode) {
                return 1;
            } else if (this.typeCode < dictionaryInfoModel.typeCode) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
