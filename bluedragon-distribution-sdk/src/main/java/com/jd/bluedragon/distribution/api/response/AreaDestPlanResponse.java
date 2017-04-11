package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * 龙门架发货关系方案
 *
 * @param <T>
 */
public class AreaDestPlanResponse<T> extends JdResponse {

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
