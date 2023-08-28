package com.jd.bluedragon.common.dto.inventory;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;


/**
 * 找货任务
 */
public class InventoryTaskQueryReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -6051001368608203945L;

    /**
     * 扫描单号
     */
    private String bizId;


    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }
}
