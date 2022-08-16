package com.jd.bluedragon.common.dto.jyexpection.response;

import java.util.List;

/**
 * 异常任务详情-暂存信息
 */
public class ExpTaskDetailCacheDto extends ExpTaskDetailDto {

    private String expBarcode;

    // 验货-上游发货批次号
    private List<String> recentSendCodeList;

    // 发货-目的场地
    private List<Integer> recentReceiveSiteList;

    public String getExpBarcode() {
        return expBarcode;
    }

    public void setExpBarcode(String expBarcode) {
        this.expBarcode = expBarcode;
    }

    public List<String> getRecentSendCodeList() {
        return recentSendCodeList;
    }

    public void setRecentSendCodeList(List<String> recentSendCodeList) {
        this.recentSendCodeList = recentSendCodeList;
    }

    public List<Integer> getRecentReceiveSiteList() {
        return recentReceiveSiteList;
    }

    public void setRecentReceiveSiteList(List<Integer> recentReceiveSiteList) {
        this.recentReceiveSiteList = recentReceiveSiteList;
    }
}
