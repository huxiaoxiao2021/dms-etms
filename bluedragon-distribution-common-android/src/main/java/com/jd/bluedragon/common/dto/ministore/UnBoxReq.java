package com.jd.bluedragon.common.dto.ministore;

import java.io.Serializable;

public class UnBoxReq implements Serializable {
    private static final long serialVersionUID = -7084771505168281914L;
    private Long miniStoreBindRelationId;
    private String boxCode;
    private String updateUser;
    private Long updateUserCode;
    private String errMsg;
    private Long createSiteCode;
    private Integer unboxCount;

    public Integer getUnboxCount() {
        return unboxCount;
    }

    public void setUnboxCount(Integer unboxCount) {
        this.unboxCount = unboxCount;
    }

    public Long getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Long createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
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
