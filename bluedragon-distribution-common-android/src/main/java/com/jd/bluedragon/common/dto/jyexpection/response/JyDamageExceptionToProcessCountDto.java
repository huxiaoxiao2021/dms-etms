package com.jd.bluedragon.common.dto.jyexpection.response;

import java.io.Serializable;

public class JyDamageExceptionToProcessCountDto implements Serializable {
    /**
     * 待处理破损异常数量
     */
    private Integer toProcessCount;
    /**
     * 待处理破损异常新增数量
     */
    private Integer toProcessAddCount;

    public Integer getToProcessCount() {
        return toProcessCount;
    }

    public void setToProcessCount(Integer toProcessCount) {
        this.toProcessCount = toProcessCount;
    }

    public Integer getToProcessAddCount() {
        return toProcessAddCount;
    }

    public void setToProcessAddCount(Integer toProcessAddCount) {
        this.toProcessAddCount = toProcessAddCount;
    }
}
