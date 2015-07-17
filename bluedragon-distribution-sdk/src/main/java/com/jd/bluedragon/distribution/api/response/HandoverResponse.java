package com.jd.bluedragon.distribution.api.response;

import java.util.ArrayList;
import java.util.List;

import com.jd.bluedragon.distribution.api.JdResponse;

public class HandoverResponse extends JdResponse {
	private static final long serialVersionUID = 7897205746166411069L;

	List<HandoverDetailResponse> data = new ArrayList<HandoverDetailResponse>();

	public List<HandoverDetailResponse> getData() {
		return data;
	}

	public void setData(List<HandoverDetailResponse> data) {
		this.data = data;
	}


}
