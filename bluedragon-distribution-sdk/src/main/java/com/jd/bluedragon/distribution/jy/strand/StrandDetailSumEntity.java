package com.jd.bluedragon.distribution.jy.strand;

import java.io.Serializable;

/**
 * 拣运滞留明细bizID汇总VO
 *
 * @author hujiping
 * @date 2023/4/20 7:54 PM
 */
public class StrandDetailSumEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务主键
     */
    private String bizId;

    /**
     * 任务下所有容器内数量总和
     */
    private Integer totalContainerInnerCount;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Integer getTotalContainerInnerCount() {
        return totalContainerInnerCount;
    }

    public void setTotalContainerInnerCount(Integer totalContainerInnerCount) {
        this.totalContainerInnerCount = totalContainerInnerCount;
    }
}
