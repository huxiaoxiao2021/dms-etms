package com.jd.bluedragon.distribution.b2bRouter.domain;

import java.io.Serializable;

/**
 * Created by xumei3 on 2018/2/26.
 */
public class B2BSite implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer B2BSiteType;
    private Integer B2BSiteCode;
    private String B2BSiteName;

    public Integer getB2BSiteCode() {
        return B2BSiteCode;
    }

    public void setB2BSiteCode(Integer b2BSiteCode) {
        B2BSiteCode = b2BSiteCode;
    }

    public String getB2BSiteName() {
        return B2BSiteName;
    }

    public void setB2BSiteName(String b2BSiteName) {
        B2BSiteName = b2BSiteName;
    }

    public Integer getB2BSiteType() {
        return B2BSiteType;
    }

    public void setB2BSiteType(Integer b2BSiteType) {
        B2BSiteType = b2BSiteType;
    }
}
