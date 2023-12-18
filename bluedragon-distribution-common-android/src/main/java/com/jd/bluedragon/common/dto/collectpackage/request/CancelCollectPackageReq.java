package com.jd.bluedragon.common.dto.collectpackage.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class CancelCollectPackageReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -5751334349642852191L;

    /**
     * 扫描单号
     */
    private String barCode;
    /**
     * 箱号
     */
    private String boxCode;

    private String bizId;

    /**
     * 是否整箱全部取消
     */
    private boolean cancelAllFlag;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public boolean getCancelAllFlag() {
        return cancelAllFlag;
    }

    public void setCancelAllFlag(boolean cancelAllFlag) {
        this.cancelAllFlag = cancelAllFlag;
    }
}
