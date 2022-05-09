package com.jd.bluedragon.common.dto.carTask.response;

import java.io.Serializable;

/**
 * 车辆任务的目的站点
 */
public class CarTaskEndNodeResponse implements Serializable {

    /**
     * 目的站点编码
     */
    private Integer endNodeCode;

    /**
     * 目的站点名称
     */
    private String endNodeName;

    public Integer getEndNodeCode() {
        return endNodeCode;
    }

    public void setEndNodeCode(Integer endNodeCode) {
        this.endNodeCode = endNodeCode;
    }

    public String getEndNodeName() {
        return endNodeName;
    }

    public void setEndNodeName(String endNodeName) {
        this.endNodeName = endNodeName;
    }
}
