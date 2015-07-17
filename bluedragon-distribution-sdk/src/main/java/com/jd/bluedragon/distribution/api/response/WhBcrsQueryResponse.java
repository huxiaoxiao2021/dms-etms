package com.jd.bluedragon.distribution.api.response;

import java.util.List;

import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * 通过运单号获得运单地址以及包裹号
 * @author huangliang
 *
 */
public class WhBcrsQueryResponse extends JdResponse{

	private static final long serialVersionUID = 6449019162737400791L;

	public WhBcrsQueryResponse(Integer code, String message, String waybillCodes) {
		super(code, message);
		this.waybillCodes = waybillCodes;
	}

	private String waybillCodes;

	public String getWaybillCodes() {
		return waybillCodes;
	}

	public void setWaybillCodes(String waybillCodes) {
		this.waybillCodes = waybillCodes;
	}

}
