package com.jd.bluedragon.distribution.send.domain;

import java.io.Serializable;
import java.util.List;

public class ThreeDeliveryResponse implements Serializable{

	private static final long serialVersionUID = 5640679967099473566L;

	/** 相应状态码 */
    private Integer code;

    /** 响应消息 */
    private String message;
    
    private List<SendThreeDetail> data;

    
    public ThreeDeliveryResponse() {
        super();
    }
    
	public ThreeDeliveryResponse(Integer code, String message,
			List<SendThreeDetail> data) {
		super();
		this.code = code;
		this.message = message;
		this.data = data;
	}


	public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SendThreeDetail> getData() {
        return data;
    }

    public void setData(List<SendThreeDetail> data) {
        this.data = data;
    }
}
