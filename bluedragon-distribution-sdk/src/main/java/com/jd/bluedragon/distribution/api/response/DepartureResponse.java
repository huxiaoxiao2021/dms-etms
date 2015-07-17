package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

import java.util.List;

/**
 * Created by dudong on 2014/12/24.
 */
public class DepartureResponse extends JdResponse{
    private List<DeparturePrintResponse> deliveryInfo;

    public List<DeparturePrintResponse> getDeliveryInfo() {
        return deliveryInfo;
    }

    public void setDeliveryInfo(List<DeparturePrintResponse> deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }
}
