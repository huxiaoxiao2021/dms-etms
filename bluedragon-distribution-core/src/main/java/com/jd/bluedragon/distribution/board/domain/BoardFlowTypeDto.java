package com.jd.bluedragon.distribution.board.domain;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/
public class BoardFlowTypeDto  implements Serializable {

    private static final long serialVersionUID = -7623509285189482980L;
    /**
     * 组板流向模式： 1单流向 ， 2 多流向
     */
    private Integer flowType;

    private Long createTimeStamp;

    public Integer getFlowType() {
        return flowType;
    }

    public void setFlowType(Integer flowType) {
        this.flowType = flowType;
    }

    public Long getCreateTimeStamp() {
        return createTimeStamp;
    }

    public void setCreateTimeStamp(Long createTimeStamp) {
        this.createTimeStamp = createTimeStamp;
    }
}
