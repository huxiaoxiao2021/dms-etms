package com.jd.bluedragon.distribution.box.domain;

import java.io.Serializable;

public class GenerateBoxReq implements Serializable {
    private static final long serialVersionUID = 4707330756075087860L;

    /**
     * 来源
     */
    private String source;
    /**
     * 箱号类型
     */
    private String boxType;
    /**
     * 箱号子类型
     */
    private String boxSubType;
    /**
     * 生成数量
     */
    private Integer count;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getBoxType() {
        return boxType;
    }

    public void setBoxType(String boxType) {
        this.boxType = boxType;
    }

    public String getBoxSubType() {
        return boxSubType;
    }

    public void setBoxSubType(String boxSubType) {
        this.boxSubType = boxSubType;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
