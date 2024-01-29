package com.jd.bluedragon.distribution.box.domain;

import java.io.Serializable;
public class StoreInfo implements Serializable {
    private static final long serialVersionUID = -8989777166601322844L;

    /**
     * 库房类型
     */
   private String storeType;
    /**
     * 配送中心id
     */
   private Integer cky2;
    /**
     * 库房id
     */
   private Integer storeId;

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public Integer getCky2() {
        return cky2;
    }

    public void setCky2(Integer cky2) {
        this.cky2 = cky2;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }
}
