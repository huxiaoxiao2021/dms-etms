package com.jd.bluedragon.common.dto.carTask.response;

import java.io.Serializable;

/**
 * 车辆任务的目的站点
 */
public class CarTaskEndNodeResponse implements Serializable {

    /**
     * 目的站点编码
     */
    private String endNodeCode;

    /**
     * 目的站点名称
     */
    private String endNodeName;

    public String getEndNodeCode() {
        return endNodeCode;
    }

    public void setEndNodeCode(String endNodeCode) {
        this.endNodeCode = endNodeCode;
    }

    public String getEndNodeName() {
        return endNodeName;
    }

    public void setEndNodeName(String endNodeName) {
        this.endNodeName = endNodeName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CarTaskEndNodeResponse)) return false;
        CarTaskEndNodeResponse that = (CarTaskEndNodeResponse) o;
        return getEndNodeCode().equals(that.getEndNodeCode()) && getEndNodeName().equals(that.getEndNodeName());
    }

    @Override
    public int hashCode() {
        int result = endNodeCode.hashCode();
        result = 31 * result + endNodeName.hashCode();
        return result;
    }
}
