package com.jd.bluedragon.distribution.seal.domain;

import lombok.Data;

import java.util.Date;

@Data
public class BatchSendStatusChange {

    public BatchSendStatusChange(String batchCode, int status) {
        this.batchCode = batchCode;
        this.status = status;
        this.changeTime = new Date();
    }

    /**
     * 批次号
     */
    private String batchCode;
    /**
     * 批次号状态  0： 已使用  1：取消使用
     */
    private int status;
    /**
     * 批次号改变时间
     */
    private Date changeTime;

}


