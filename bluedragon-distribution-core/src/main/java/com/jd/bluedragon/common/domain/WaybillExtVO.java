package com.jd.bluedragon.common.domain;

import java.io.Serializable;

/**
 * 运单额外属性
 *
 * @author hujiping
 * @date 2023/8/2 10:32 AM
 */
public class WaybillExtVO implements Serializable {

    // 报关类型
    private String clearanceType;

    // 运单的始发流向
    private String startFlowDirection;

    // 运单的目的流向
    private String endFlowDirection;

    public WaybillExtVO clearanceType(String clearanceType){
        this.clearanceType = clearanceType;
        return this;
    }

    public WaybillExtVO startFlowDirection(String startFlowDirection){
        this.startFlowDirection = startFlowDirection;
        return this;
    }

    public WaybillExtVO endFlowDirection(String endFlowDirection){
        this.endFlowDirection = endFlowDirection;
        return this;
    }

    public String getClearanceType() {
        return clearanceType;
    }

    public void setClearanceType(String clearanceType) {
        this.clearanceType = clearanceType;
    }

    public String getStartFlowDirection() {
        return startFlowDirection;
    }

    public void setStartFlowDirection(String startFlowDirection) {
        this.startFlowDirection = startFlowDirection;
    }

    public String getEndFlowDirection() {
        return endFlowDirection;
    }

    public void setEndFlowDirection(String endFlowDirection) {
        this.endFlowDirection = endFlowDirection;
    }
}
