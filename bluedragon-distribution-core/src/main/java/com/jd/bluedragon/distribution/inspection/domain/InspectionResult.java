package com.jd.bluedragon.distribution.inspection.domain;

import java.io.Serializable;

/**
 * Created by hujiping on 2018/3/21.
 */
public class InspectionResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /*库位号*/
    private String storageCode;

    /*PDA验货提示语*/
    private String hintMessage;

    public InspectionResult(String storageCode) {
        this.storageCode = storageCode;
    }

    public String getStorageCode() {
        return storageCode;
    }

    public void setStorageCode(String storageCode) {
        this.storageCode = storageCode;
    }

    public String getHintMessage() {
        return hintMessage;
    }

    public void setHintMessage(String hintMessage) {
        this.hintMessage = hintMessage;
    }
}
