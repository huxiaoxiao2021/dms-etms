package com.jd.bluedragon.distribution.alpha;

/**
 * Created by wuzuxiang on 2016/9/6.
 */
public class VersionIdListRequest<T> {

    /**
     * versionID列表
     */
    private T list;

    public T getList() {
        return list;
    }

    public void setList(T list) {
        this.list = list;
    }

}
