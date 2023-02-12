package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;

/**
 * 发货任务某种状态下的线路类型统计指标
 **/
public class JyBizTaskSendLineTypeCountDto implements Serializable {

    private static final long serialVersionUID = 4959005149526416723L;

    /**
     * 线路类型
     */
    private Integer lineType;

    /**
     * 总数
     */
    private Long total;

    public Integer getLineType() {
        return lineType;
    }

    public void setLineType(Integer lineType) {
        this.lineType = lineType;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
