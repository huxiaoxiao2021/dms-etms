package com.jd.bluedragon.distribution.express.domain;

import java.io.Serializable;

/**
 * @comments 包裹实体类
 * @author zhangleqi
 * @date 2017/11/14
 */

public class ExpressBoxDetail implements Serializable {

    private static final long serialVersionUID = 9015640463647589701L;
    private String boxCode;
    private int packageSize;

    public ExpressBoxDetail() {
    }

    public ExpressBoxDetail(String boxCode, int packageSize) {
        this.boxCode = boxCode;
        this.packageSize = packageSize;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public int getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(int packageSize) {
        this.packageSize = packageSize;
    }
}
