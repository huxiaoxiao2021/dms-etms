package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * Created by yangbo7 on 2016/3/28.
 */
public class RemoteAccessResponse<T> extends JdResponse {

    // 返回的数据
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
