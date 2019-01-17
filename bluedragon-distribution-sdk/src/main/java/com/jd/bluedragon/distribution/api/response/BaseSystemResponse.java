package com.jd.bluedragon.distribution.api.response;

import java.io.Serializable;

/**
 * @ClassName: BaseSystemResponse
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2019/1/8 11:09
 */
public class BaseSystemResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 服务器时间 */
    private String systemTime;

    public String getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(String systemTime) {
        this.systemTime = systemTime;
    }
}
