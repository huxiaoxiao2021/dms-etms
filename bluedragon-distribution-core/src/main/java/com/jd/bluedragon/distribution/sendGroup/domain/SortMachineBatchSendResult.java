package com.jd.bluedragon.distribution.sendGroup.domain;

import com.jd.bluedragon.distribution.sortscheme.domain.SortSchemeDetail;

import java.util.Date;

/**
 * Created by jinjingcheng on 2017/7/6.
 */
public class SortMachineBatchSendResult {
     private  SortSchemeDetail sortSchemeDetail;
    private String sendCode;//批次目的地
    private Date sendCodeCreateTime;

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public SortSchemeDetail getSortSchemeDetail() {
        return sortSchemeDetail;
    }

    public void setSortSchemeDetail(SortSchemeDetail sortSchemeDetail) {
        this.sortSchemeDetail = sortSchemeDetail;
    }

    public Date getSendCodeCreateTime() {
        return sendCodeCreateTime;
    }

    public void setSendCodeCreateTime(Date sendCodeCreateTime) {
        this.sendCodeCreateTime = sendCodeCreateTime;
    }
}
