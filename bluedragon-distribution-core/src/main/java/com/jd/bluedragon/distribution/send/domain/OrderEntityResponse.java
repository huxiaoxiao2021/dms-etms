package com.jd.bluedragon.distribution.send.domain;

import java.io.Serializable;
import java.util.List;

public class OrderEntityResponse implements Serializable{

    private static final long serialVersionUID = 6782494991811332161L;
    
    /** 相应状态码 */
    private Integer code;

    /** 响应消息 */
    private String message;
    
    private List<OrdersEntity> data;

    
    public OrderEntityResponse() {
        super();
    }
    
	public OrderEntityResponse(Integer code, String message,
			List<OrdersEntity> data) {
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

    public List<OrdersEntity> getData() {
        return data;
    }

    public void setData(List<OrdersEntity> data) {
        this.data = data;
    }
}
