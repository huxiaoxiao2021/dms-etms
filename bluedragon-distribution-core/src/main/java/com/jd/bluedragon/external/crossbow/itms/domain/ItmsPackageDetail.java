package com.jd.bluedragon.external.crossbow.itms.domain;

import java.io.Serializable;

/**
 * @ClassName ItmsPackageDetail
 * @Description
 * @Author wyh
 * @Date 2021/6/8 10:20
 **/
public class ItmsPackageDetail implements Serializable {

    private static final long serialVersionUID = 3132717519377324686L;

    private String packageCode;

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }
}
