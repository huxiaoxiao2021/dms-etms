package com.jd.bluedragon.common.dto.ministore;

import java.io.Serializable;

public class UnBoxReq implements Serializable {
    private static final long serialVersionUID = -7084771505168281914L;
    private Long miniStoreBindRelationId;
    private String boxCode;

    public Long getMiniStoreBindRelationId() {
        return miniStoreBindRelationId;
    }

    public void setMiniStoreBindRelationId(Long miniStoreBindRelationId) {
        this.miniStoreBindRelationId = miniStoreBindRelationId;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }
}
