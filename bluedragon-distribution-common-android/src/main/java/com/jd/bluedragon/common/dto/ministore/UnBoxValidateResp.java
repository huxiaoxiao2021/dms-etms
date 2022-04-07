package com.jd.bluedragon.common.dto.ministore;

import java.io.Serializable;

public class UnBoxValidateResp implements Serializable {
    private static final long serialVersionUID = -4198321416624957318L;
    private Long   miniStoreBindRelationId;
    private String storeCode;
    private String iceBoardCode1;
    private String iceBoardCode2;
    private String boxCode;
    private Integer sortCount;
    private Byte state;

    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }

    public Integer getSortCount() {
        return sortCount;
    }

    public void setSortCount(Integer sortCount) {
        this.sortCount = sortCount;
    }

    public Long getMiniStoreBindRelationId() {
        return miniStoreBindRelationId;
    }

    public void setMiniStoreBindRelationId(Long miniStoreBindRelationId) {
        this.miniStoreBindRelationId = miniStoreBindRelationId;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getIceBoardCode1() {
        return iceBoardCode1;
    }

    public void setIceBoardCode1(String iceBoardCode1) {
        this.iceBoardCode1 = iceBoardCode1;
    }

    public String getIceBoardCode2() {
        return iceBoardCode2;
    }

    public void setIceBoardCode2(String iceBoardCode2) {
        this.iceBoardCode2 = iceBoardCode2;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }
}
