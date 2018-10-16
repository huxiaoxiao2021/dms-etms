package com.jd.bluedragon.distribution.video.domain;

import java.io.Serializable;

public class VideoRequest implements Serializable {

    private static final long serialVersionUID = 3809791575103607077L;

    /**当前分拣中心Id*/
    private Integer createSiteCode;

    /**操作节点类型（验货、分拣、称重量方、发货）*/
    private Integer operateType;

    /**包裹号*/
    private String packageCode;

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }
}
