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

    /**
     * 操作来源 1：pda 2:分拣机
     * @return
     */
    private Integer bizSource;

    /**
     * 版本：做数据隔离
     */
    private Integer version;

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

    public Integer getBizSource() {
        return bizSource;
    }

    public void setBizSource(Integer bizSource) {
        this.bizSource = bizSource;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
