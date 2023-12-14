package com.jd.bluedragon.common.dto.work;

import java.io.Serializable;

public class WorkGridCandidateData implements Serializable {
    private static final long serialVersionUID = 1L;
    private String erp;
    //名字
    private String name;
    //状态 1 正常 2未排班不可选
    private Integer status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getErp() {
        return erp;
    }

    public void setErp(String erp) {
        this.erp = erp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
