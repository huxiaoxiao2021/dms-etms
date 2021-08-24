package com.jd.bluedragon.common.dto.board.request;

import com.jd.bluedragon.common.dto.base.request.OperatorInfo;
import java.io.Serializable;

/**
 * 创建或获取可用的板号
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-08-20 09:46:09 周五
 */
public class AddOrGetVirtualBoardPo implements Serializable {

    private static final long serialVersionUID = 4471111251943477539L;

    /**
     * 操作人信息
     */
    private OperatorInfo operatorInfo;

    /**
     * 目的ID
     */
    private Integer destinationId;

    /**
     * 最多流向个数
     */
    private Integer maxDestinationCount;

    public OperatorInfo getOperatorInfo() {
        return operatorInfo;
    }

    public AddOrGetVirtualBoardPo setOperatorInfo(OperatorInfo operatorInfo) {
        this.operatorInfo = operatorInfo;
        return this;
    }

    public Integer getDestinationId() {
        return destinationId;
    }

    public AddOrGetVirtualBoardPo setDestinationId(Integer destinationId) {
        this.destinationId = destinationId;
        return this;
    }

    public Integer getMaxDestinationCount() {
        return maxDestinationCount;
    }

    public AddOrGetVirtualBoardPo setMaxDestinationCount(Integer maxDestinationCount) {
        this.maxDestinationCount = maxDestinationCount;
        return this;
    }
}
