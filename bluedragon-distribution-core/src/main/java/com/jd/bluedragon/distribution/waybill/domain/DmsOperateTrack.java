package com.jd.bluedragon.distribution.waybill.domain;


import com.jd.etms.waybill.dto.BdTraceDto;

import java.io.Serializable;

/**
 * 视频追溯-操作流水与全程跟踪通用实体
 */
public class DmsOperateTrack implements Serializable {

    private static final long serialVersionUID = 5029681388784883608L;

    /**
     * 操作流水表主键
     */
    private Long operateFlowId;

    /**
     * 全程跟踪详细内容
     */
    private BdTraceDto bdTraceDto;

    public Long getOperateFlowId() {
        return operateFlowId;
    }

    public void setOperateFlowId(Long operateFlowId) {
        this.operateFlowId = operateFlowId;
    }

    public BdTraceDto getBdTraceDto() {
        return bdTraceDto;
    }

    public void setBdTraceDto(BdTraceDto bdTraceDto) {
        this.bdTraceDto = bdTraceDto;
    }
}