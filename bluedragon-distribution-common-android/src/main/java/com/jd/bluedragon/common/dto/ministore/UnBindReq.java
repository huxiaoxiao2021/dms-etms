package com.jd.bluedragon.common.dto.ministore;

import java.io.Serializable;

public class UnBindReq implements Serializable {
    private static final long serialVersionUID = -5895864322798459253L;
    private Long miniStoreBindRelationId;
    private String updateUser;
    private Long updateUserCode;

    public Long getMiniStoreBindRelationId() {
        return miniStoreBindRelationId;
    }

    public void setMiniStoreBindRelationId(Long miniStoreBindRelationId) {
        this.miniStoreBindRelationId = miniStoreBindRelationId;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Long getUpdateUserCode() {
        return updateUserCode;
    }

    public void setUpdateUserCode(Long updateUserCode) {
        this.updateUserCode = updateUserCode;
    }
}
