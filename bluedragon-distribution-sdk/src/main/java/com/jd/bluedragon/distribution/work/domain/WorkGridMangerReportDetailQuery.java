package com.jd.bluedragon.distribution.work.domain;

import java.io.Serializable;


public class WorkGridMangerReportDetailQuery implements Serializable {
    private String bizId;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }
}
