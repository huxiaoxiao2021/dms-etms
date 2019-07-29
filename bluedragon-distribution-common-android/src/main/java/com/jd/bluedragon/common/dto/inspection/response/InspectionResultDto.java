package com.jd.bluedragon.common.dto.inspection.response;

import java.io.Serializable;

/**
 * @author : xumigen
 * @date : 2019/7/29
 */
public class InspectionResultDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /*库位号*/
    private String storageCode;

    /*PDA验货提示语*/
    private String hintMessage;

    public InspectionResultDto(String storageCode) {
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
