package com.jd.bluedragon.distribution.api.response;

import java.util.List;

import com.jd.bluedragon.distribution.api.JdResponse;

public class RefundReasonResponse extends JdResponse {
	
	private static final long serialVersionUID = -1758806618288230062L;

	private List<RefundReason> refundReason;
	
	public List<RefundReason> getRefundReason() {
		return this.refundReason;
	}
	
	public void setRefundReason(List<RefundReason> refundReason) {
		this.refundReason = refundReason;
	}
	
	
}
