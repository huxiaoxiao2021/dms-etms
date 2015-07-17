package com.jd.bluedragon.distribution.api.response;

import java.util.List;

import com.jd.bluedragon.distribution.api.JdResponse;

public class ProductResponse extends JdResponse {
    
    private static final long serialVersionUID = 5347478872619981207L;
    
    public static final Integer CODE_ORDER_NOT_FOUND = 26000;
    public static final String MESSAGE_ORDER_NOT_FOUND = "无订单信息";
    
    private List<Product> data;
    
    public List<Product> getData() {
        return this.data;
    }
    
    public void setData(List<Product> data) {
        this.data = data;
    }
    
}
