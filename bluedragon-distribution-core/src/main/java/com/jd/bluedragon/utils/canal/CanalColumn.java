package com.jd.bluedragon.utils.canal;

import java.io.Serializable;

/**
 * Created by zhanglei51 on 2016/11/28.
 * canal解析类 canal列属性
 */
public class CanalColumn implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;    //列明
    private String value;   //列的值
    private boolean update; //是否更新
    private int sqlType;    //列的类型
    private boolean isKey;  //是否key
    private boolean isNull; //是够为空
    private int index;      //索引
    private int length;     //长度

    public CanalColumn() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isUpdate() {
        return this.update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public int getSqlType() {
        return this.sqlType;
    }

    public void setSqlType(int sqlType) {
        this.sqlType = sqlType;
    }

    public boolean isKey() {
        return this.isKey;
    }

    public void setKey(boolean key) {
        this.isKey = key;
    }

    public boolean isNull() {
        return this.isNull;
    }

    public void setNull(boolean aNull) {
        this.isNull = aNull;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("CanalColumn{");
        sb.append("name=\'").append(this.name).append('\'');
        sb.append(", value=\'").append(this.value).append('\'');
        sb.append(", update=").append(this.update);
        sb.append(", sqlType=").append(this.sqlType);
        sb.append(", isKey=").append(this.isKey);
        sb.append(", isNull=").append(this.isNull);
        sb.append(", index=").append(this.index);
        sb.append(", length=").append(this.length);
        sb.append('}');
        return sb.toString();
    }
}
