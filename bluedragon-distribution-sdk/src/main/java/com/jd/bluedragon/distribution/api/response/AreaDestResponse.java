package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

import java.util.List;

/**
 * 区域批次目的地响应结果
 * <p>
 * Created by lixin39 on 2016/12/9.
 */
public class AreaDestResponse<T> extends JdResponse {

    /**
     * 结果数据
     */
    private T data;


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
