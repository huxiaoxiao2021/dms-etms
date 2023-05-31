package com.jd.bluedragon.common.dto.jyexpection.request;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/31 16:32
 * @Description:异常任务指派
 */
public class ExpTaskAssignRequest implements Serializable {

    /**
     * 异常任务主键
     */
    private String bizId;

    /**
     * 指派人erp
     */
    private String assignHandlerErp;

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
}
