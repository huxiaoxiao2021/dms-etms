package com.jd.bluedragon.common.dto.ministore;

import java.io.Serializable;

public class BindAndNoSortTaskReq implements Serializable {
    private static final long serialVersionUID = 7557805799773664419L;
    private Long createUserCode;
    private String createUser;
    private Integer pageNo;
    private Integer pageSize;

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Long getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Long createUserCode) {
        this.createUserCode = createUserCode;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
