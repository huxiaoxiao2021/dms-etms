package com.jd.bluedragon.distribution.external.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @author pengchong28
 * @description 组板任务司机违规举报req
 * @date 2024/4/16
 */
public class QueryDriverViolationReportingReq implements Serializable {
    private List<String> bizIdList;

    public List<String> getBizIdList() {
        return bizIdList;
    }

    public void setBizIdList(List<String> bizIdList) {
        this.bizIdList = bizIdList;
    }
}
