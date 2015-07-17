package com.jd.bluedragon.distribution.api.response;

import java.io.Serializable;

/**
 * 原包发货响应对象
 * Created by wangtingwei on 2015/3/13.
 */
public class PackageSendResponse implements Serializable {


    private static final long serialVersionUID =  1;

    /**
     * 发货状态码：
     * 1：发货成功
     * 2：发货失败
     * 4：需要用户手动确认发货[根据提示]
     */
    private int key;

    /**
     * 发货结果提示信息
     *
     */
    private String value;

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
