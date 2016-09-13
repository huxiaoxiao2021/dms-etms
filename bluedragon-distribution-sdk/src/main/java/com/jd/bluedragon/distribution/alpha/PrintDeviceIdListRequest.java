package com.jd.bluedragon.distribution.alpha;

/**
 * Created by wuzuxiang on 2016/9/1.
 */
public class PrintDeviceIdListRequest<T> {

    /**
     * ID列表
     */
    private T list;

    public T getList() {
        return list;
    }

    public void setList(T list) {
        this.list = list;
    }
}
