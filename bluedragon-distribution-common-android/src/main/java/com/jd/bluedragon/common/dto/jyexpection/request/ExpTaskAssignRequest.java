package com.jd.bluedragon.common.dto.jyexpection.request;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/31 16:32
 * @Description:异常任务指派
 */
public class ExpTaskAssignRequest extends ExpBaseReq implements Serializable {

    /**
     * 异常任务主键
     */
    private List<String> bizIds;

    /**
     * 指派人处理人erp
     */
    private String assignHandlerErp;



    public List<String> getBizIds() {
        return bizIds;
    }

    public void setBizIds(List<String> bizIds) {
        this.bizIds = bizIds;
    }

    public String getAssignHandlerErp() {
        return assignHandlerErp;
    }

    public void setAssignHandlerErp(String assignHandlerErp) {
        this.assignHandlerErp = assignHandlerErp;
    }

}
