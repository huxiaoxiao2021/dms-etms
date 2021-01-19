package com.jd.bluedragon.core.jmq.domain;

import java.io.Serializable;

/**
 * @program: bluedragon-distribution
 * @description: 卸车完成消息
 * @author: wuming
 * @create: 2020-12-31 10:53
 */
public class UnloadCarCompleteMqDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 封车号
     */
    private String sealCarCode;
    /**
     * 任务状态
     */
    private Integer status;

    public UnloadCarCompleteMqDto() {
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
