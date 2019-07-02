package com.jd.bluedragon.common.dto.blockcar.request;

import java.io.Serializable;

/**
 * CheckTransportCodeRequest
 * 任务简码和运力资源编码校验运力资源编码请求参数
 * @author jiaowenqiang
 * @date 2019/6/25
 */
public class CheckTransportCodeRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 任务编码
     */
    private String transWorkItemCode;

    /**
     * 运力编码
     */
    private String transportCode;

    @Override
    public String toString() {
        return "CheckTransportCodeRequest{" +
                "transWorkItemCode='" + transWorkItemCode + '\'' +
                ", transportCode='" + transportCode + '\'' +
                '}';
    }

    public String getTransWorkItemCode() {
        return transWorkItemCode;
    }

    public void setTransWorkItemCode(String transWorkItemCode) {
        this.transWorkItemCode = transWorkItemCode;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }
}
