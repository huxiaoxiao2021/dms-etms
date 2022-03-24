package com.jd.bluedragon.common.dto.ministore;

import java.util.List;

public class SealBoxReq {
    private Long   miniStoreBindRelationId;//绑定关系id
    private String storeCode;
    private List<String> iceBoardCodes;
    private String boxCode;
    private List<String> packageCodes;
    private String updateUser;
    private Long updateUserCode;

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

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public List<String> getIceBoardCodes() {
        return iceBoardCodes;
    }

    public void setIceBoardCodes(List<String> iceBoardCodes) {
        this.iceBoardCodes = iceBoardCodes;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public List<String> getPackageCodes() {
        return packageCodes;
    }

    public void setPackageCodes(List<String> packageCodes) {
        this.packageCodes = packageCodes;
    }
}
