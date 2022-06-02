package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName SendingVehicle
 * @Description
 * @Author wyh
 * @Date 2022/5/18 19:46
 **/
public class SendingVehicle extends BaseSendVehicle{

    private static final long serialVersionUID = 4809847037430438682L;

    /**
     * 装载率
     */
    private BigDecimal loadRate;

    /**
     * 发货流向
     */
    private List<SendVehicleDetail> sendDestList;

    public BigDecimal getLoadRate() {
        return loadRate;
    }

    public void setLoadRate(BigDecimal loadRate) {
        this.loadRate = loadRate;
    }

    public List<SendVehicleDetail> getSendDestList() {
        return sendDestList;
    }

    public void setSendDestList(List<SendVehicleDetail> sendDestList) {
        this.sendDestList = sendDestList;
    }
}
