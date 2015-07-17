package com.jd.bluedragon.distribution.api.request;

import java.util.List;

import com.jd.bluedragon.distribution.api.JdRequest;

public class DeliveryBatchRequest extends JdRequest {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -2206326236799257156L;

    
    /** 箱号 */
    private List<DeliveryRequest> deliverys;
    

	public List<DeliveryRequest> getDeliverys() {
		return deliverys;
	}

	public void setDeliverys(List<DeliveryRequest> deliverys) {
		this.deliverys = deliverys;
	}

}
