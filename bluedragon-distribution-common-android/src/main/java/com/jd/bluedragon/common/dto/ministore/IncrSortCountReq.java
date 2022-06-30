package com.jd.bluedragon.common.dto.ministore;

import java.io.Serializable;

public class IncrSortCountReq implements Serializable {
    private static final long serialVersionUID = -1234653166961019979L;
    private Long id;
    private String updateUser;
    private Long updateUserCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
