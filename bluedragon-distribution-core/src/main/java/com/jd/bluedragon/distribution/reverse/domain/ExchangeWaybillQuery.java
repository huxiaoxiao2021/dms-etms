package com.jd.bluedragon.distribution.reverse.domain;

import java.util.List;

/**
 * @author liwenji
 * @description TODO
 * @date 2022-08-31 18:28
 */
public class ExchangeWaybillQuery extends ExchangeWaybillDto{
    /**
     * 不需要微笑字段
     */
    private List<String> hideInfo;

    public List<String> getHideInfo() {
        return hideInfo;
    }

    public void setHideInfo(List<String> hideInfo) {
        this.hideInfo = hideInfo;
    }
}
