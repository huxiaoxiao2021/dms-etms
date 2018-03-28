package com.jd.bluedragon.distribution.half.domain;

import java.io.Serializable;

public class PackageHalfDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String packageCode;

    private String resultType;  /** 配送结果（1-妥投，2-拒收，3-丢失） */

    private String reasonType; /** 拒收原因（1-破损 ,2- 丢失,3- 报废 ,4-客户原因,5- 其他） */

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getReasonType() {
        return reasonType;
    }

    public void setReasonType(String reasonType) {
        this.reasonType = reasonType;
    }


}
