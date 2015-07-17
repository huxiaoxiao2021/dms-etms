package com.jd.bluedragon.distribution.api.response;

import java.util.ArrayList;
import java.util.List;

import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * Web
 * @author zhuchao
 *
 */
public class SendBoxResponse extends JdResponse {
	
	private static final long serialVersionUID = 7890205746166411069L;
	
	private List<SendBoxDetailResponse> detail = new ArrayList<SendBoxDetailResponse>();
	
	public List<SendBoxDetailResponse> getDetail() {
		return this.detail;
	}
	
	public void setDetail(List<SendBoxDetailResponse> detail) {
		this.detail = detail;
	}
}
