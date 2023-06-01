package com.jd.bluedragon.distribution.jy.exception;

import java.io.Serializable;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/6/1 11:28
 * @Description: 异常任务指派ＭＱ
 */
public class  JyAssignExpTaskMQ implements Serializable {

    /**
     * 异常任务主键
     */
    private String bizId;

    /**
     * 指派人erp
     */
    private String assignHandlerErp;

    /**
     * 负责人erp
     */
    private String principalErp;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getAssignHandlerErp() {
        return assignHandlerErp;
    }

    public void setAssignHandlerErp(String assignHandlerErp) {
        this.assignHandlerErp = assignHandlerErp;
    }

    public String getPrincipalErp() {
        return principalErp;
    }

    public void setPrincipalErp(String principalErp) {
        this.principalErp = principalErp;
    }
}
