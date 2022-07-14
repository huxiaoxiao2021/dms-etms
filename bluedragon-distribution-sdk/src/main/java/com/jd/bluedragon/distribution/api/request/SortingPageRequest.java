package com.jd.bluedragon.distribution.api.request;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 * @author : caozhixing3
 * @version V1.0
 * @Project: bluedragon-distribution
 * @Package com.jd.bluedragon.distribution.api.request
 * @Description:
 * @date Date : 2022年07月14日 14:31
 */
public class SortingPageRequest extends BasePagerCondition {
    private static final long serialVersionUID = 8900218370299464985L;
    /**
     * 箱号
     */
    private String boxCode;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }
}
