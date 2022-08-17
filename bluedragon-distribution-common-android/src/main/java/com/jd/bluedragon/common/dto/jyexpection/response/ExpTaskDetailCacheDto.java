package com.jd.bluedragon.common.dto.jyexpection.response;

import java.util.Collection;
import java.util.List;

/**
 * 异常任务详情-暂存信息
 */
public class ExpTaskDetailCacheDto extends ExpTaskDetailDto {

    private String expBarcode;

    // 验货-上游发货批次号
    private Collection<String> recentSendCodeList;

    // 发货-目的场地
    private Collection<Integer> recentReceiveSiteList;

    public String getExpBarcode() {
        return expBarcode;
    }

    public void setExpBarcode(String expBarcode) {
        this.expBarcode = expBarcode;
    }

    public Collection<String> getRecentSendCodeList() {
        return recentSendCodeList;
    }

    public void setRecentSendCodeList(Collection<String> recentSendCodeList) {
        this.recentSendCodeList = recentSendCodeList;
    }

    public Collection<Integer> getRecentReceiveSiteList() {
        return recentReceiveSiteList;
    }

    public void setRecentReceiveSiteList(Collection<Integer> recentReceiveSiteList) {
        this.recentReceiveSiteList = recentReceiveSiteList;
    }
}
