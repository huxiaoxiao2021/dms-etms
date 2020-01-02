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

    /**
     * 笼车号
     */
    private String tabletrolleyCode;

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

    public String getTabletrolleyCode() {
        return tabletrolleyCode;
    }

    public void setTabletrolleyCode(String tabletrolleyCode) {
        this.tabletrolleyCode = tabletrolleyCode;
    }
}
