package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;
import java.util.List;

public class TransferVehicleResp implements Serializable {

    private static final long serialVersionUID = -3631878177602459614L;

    /**
     * 迁移后的批次号
     */
    private List<String> sendCodes;

    public List<String> getSendCodes() {
        return sendCodes;
    }

    public void setSendCodes(List<String> sendCodes) {
        this.sendCodes = sendCodes;
    }
}
