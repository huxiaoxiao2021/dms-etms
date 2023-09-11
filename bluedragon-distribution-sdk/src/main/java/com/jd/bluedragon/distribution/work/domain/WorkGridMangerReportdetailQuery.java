package com.jd.bluedragon.distribution.work.domain;

import java.io.Serializable;


public class WorkGridMangerReportdetailQuery implements Serializable {
    private String bizId;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }
}
