package com.jd.bluedragon.common.dto.work;

import java.io.Serializable;

public class JyWorkGridManagerTransferData implements Serializable {

    private static final long serialVersionUID = 38398418414182340L;


    /**
     * 业务主键
     */
    private String bizId;

    /**
     * 转派责任人erp
     */
    private String erp;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getErp() {
        return erp;
    }

    public void setErp(String erp) {
        this.erp = erp;
    }
}
