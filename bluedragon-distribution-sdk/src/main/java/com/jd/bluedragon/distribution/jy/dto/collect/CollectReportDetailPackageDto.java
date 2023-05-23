package com.jd.bluedragon.distribution.jy.dto.collect;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Description //集齐报表查询明细响应包裹明细bean
 * @date
 **/
public class CollectReportDetailPackageDto implements Serializable {

    private static final long serialVersionUID = -6963372061306635997L;

    private String packageCode;

    /**
     * 包裹集齐状态
     * TysCollectStatusEnum
     */
    private Integer packageCollectStatus;

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getPackageCollectStatus() {
        return packageCollectStatus;
    }

    public void setPackageCollectStatus(Integer packageCollectStatus) {
        this.packageCollectStatus = packageCollectStatus;
    }
}
