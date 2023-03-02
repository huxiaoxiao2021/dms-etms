package com.jd.bluedragon.distribution.jy.dto.send;

import java.util.List;

/**
 * @ClassName SendVehicleTask
 * @Description
 * @Author wyh
 * @Date 2022/5/18 19:44
 **/
public class ToSendVehicle extends BaseSendVehicle {

    private static final long serialVersionUID = 6722412323456098350L;

    /**
     * 发货流向
     */
    private List<SendVehicleDetail> sendDestList;

    public List<SendVehicleDetail> getSendDestList() {
        return sendDestList;
    }

    public void setSendDestList(List<SendVehicleDetail> sendDestList) {
        this.sendDestList = sendDestList;
    }
}
