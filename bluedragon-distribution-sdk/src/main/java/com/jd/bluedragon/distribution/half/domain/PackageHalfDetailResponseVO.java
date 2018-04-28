package com.jd.bluedragon.distribution.half.domain;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 获取包裹操作明细响应报文
 */
public class PackageHalfDetailResponseVO implements Serializable{

    private static final long serialVersionUID = 1L;

    private boolean canDelievered; //是否可以操作妥投

    private boolean canReject;  //是否可以操作拒收

    private List<PackageHalfDetail> packageList;


    public boolean getCanDelievered() {
        return canDelievered;
    }

    public void setCanDelievered(boolean canDelievered) {
        this.canDelievered = canDelievered;
    }

    public boolean getCanReject() {
        return canReject;
    }

    public void setCanReject(boolean canReject) {
        this.canReject = canReject;
    }

    public List<PackageHalfDetail> getPackageList() {
        return packageList;
    }

    public void setPackageList(List<PackageHalfDetail> packageList) {
        this.packageList = packageList;
    }
}
