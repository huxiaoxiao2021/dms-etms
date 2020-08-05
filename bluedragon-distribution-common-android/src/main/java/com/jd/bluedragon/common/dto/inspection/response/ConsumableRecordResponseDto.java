package com.jd.bluedragon.common.dto.inspection.response;

import java.io.Serializable;

/**
 * @author lijie
 * @date 2020/6/18 16:16
 */
public class ConsumableRecordResponseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean isExistConsumableRecord;

    private String hintMessage;

    public boolean isExistConsumableRecord() {
        return isExistConsumableRecord;
    }

    public void setExistConsumableRecord(boolean existConsumableRecord) {
        isExistConsumableRecord = existConsumableRecord;
    }

    public String getHintMessage() {
        return hintMessage;
    }

    public void setHintMessage(String hintMessage) {
        this.hintMessage = hintMessage;
    }
}
