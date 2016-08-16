package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

import java.io.Serializable;

/**
 * Created by yangbo7 on 2016/6/22.
 */
public class SortSchemeDetailResponse<T> extends JdResponse implements Serializable {

    private static final long serialVersionUID = 7032521443830916012L;

    T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
