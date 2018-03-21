package com.jd.bluedragon.distribution.inspection.domain;

import java.io.Serializable;

/**
 * Created by hujiping on 2018/3/21.
 */
public class InspectionResult implements Serializable {

    /*库位号*/
    private String storageCode;

    public InspectionResult(String storageCode) {
        this.storageCode = storageCode;
    }

    public String getStorageCode() {
        return storageCode;
    }

    public void setStorageCode(String storageCode) {
        this.storageCode = storageCode;
    }
}
