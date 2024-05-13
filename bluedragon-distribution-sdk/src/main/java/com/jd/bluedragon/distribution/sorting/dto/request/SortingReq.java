package com.jd.bluedragon.distribution.sorting.dto.request;

import java.io.Serializable;

/**
 * 分拣请求
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-04-10 21:09:05 周三
 */
public class SortingReq implements Serializable {
    private static final long serialVersionUID = -4409334193484999741L;

    private Integer createSiteCode;

    private String packageCode;

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }
}
