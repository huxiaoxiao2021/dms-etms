package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.util.List;

/**
 * @ClassName ToSealVehicle
 * @Description
 * @Author wyh
 * @Date 2022/5/18 19:48
 **/
public class ToSealVehicle extends BaseSendVehicle{

    private static final long serialVersionUID = 5811036835296863596L;

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
