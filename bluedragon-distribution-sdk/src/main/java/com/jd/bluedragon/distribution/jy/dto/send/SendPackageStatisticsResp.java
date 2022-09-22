package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;
import java.util.List;

public class SendPackageStatisticsResp implements Serializable {

    private static final long serialVersionUID = 2651092353037678971L;

    /**
     * 包裹集合
     */
    private List<SendPackage> packageList;

    public List<SendPackage> getPackageList() {
        return packageList;
    }

    public void setPackageList(List<SendPackage> packageList) {
        this.packageList = packageList;
    }
}




