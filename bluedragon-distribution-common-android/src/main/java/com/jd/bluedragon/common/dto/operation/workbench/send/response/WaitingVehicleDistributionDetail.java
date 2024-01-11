package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.io.Serializable;
import java.util.Date;

public class WaitingVehicleDistributionDetail extends BaseSendVehicleDetail {
    private static final long serialVersionUID = 7945709655964500422L;

    /**
     * 待派车任务号
     */
    private String transJobItemCode;

    /**
     * 运力资源编码
     */
    private String transportCode;

    public String getTransJobItemCode() {
        return transJobItemCode;
    }

    public void setTransJobItemCode(String transJobItemCode) {
        this.transJobItemCode = transJobItemCode;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }
}
