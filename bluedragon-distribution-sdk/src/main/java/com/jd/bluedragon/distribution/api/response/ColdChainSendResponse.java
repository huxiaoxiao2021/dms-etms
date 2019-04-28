package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName ColdChainSendResponse
 * @date 2019/4/8
 */
public class ColdChainSendResponse<T> extends JdResponse {

    /**
     * 返回数据信息
     */
    private T data;

    public ColdChainSendResponse() {
    }

    public ColdChainSendResponse(Integer code, String message) {
        super(code, message);
    }

    public ColdChainSendResponse(Integer code, String message, T data) {
        super(code, message);
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
