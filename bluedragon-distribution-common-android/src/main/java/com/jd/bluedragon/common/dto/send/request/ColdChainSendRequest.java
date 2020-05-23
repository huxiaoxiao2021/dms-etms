package com.jd.bluedragon.common.dto.send.request;

import java.io.Serializable;
import java.util.List;

public class ColdChainSendRequest implements Serializable{

   private List<DeliveryRequest> sendList;

    public List<DeliveryRequest> getSendList() {
        return sendList;
    }

    public void setSendList(List<DeliveryRequest> sendList) {
        this.sendList = sendList;
    }
}
