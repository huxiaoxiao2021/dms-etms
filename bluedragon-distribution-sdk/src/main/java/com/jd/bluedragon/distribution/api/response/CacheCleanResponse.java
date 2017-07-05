package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

import java.io.Serializable;

/**
 * Created by zhoutao on 2017/6/13.
 */
public class CacheCleanResponse<T> extends JdResponse implements Serializable {

    private static final long serialVersionUID = 4596041401105605529L;

    T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
