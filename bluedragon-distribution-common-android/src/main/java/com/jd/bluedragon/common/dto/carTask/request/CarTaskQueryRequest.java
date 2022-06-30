package com.jd.bluedragon.common.dto.carTask.request;

import java.io.Serializable;

/**
 * 运输车辆任务查询
 */
public class CarTaskQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 始发网点编码
     */
    private String beginNodeCode;

    /**
     * 起始网点类型
     */
    private String startNodeType;

    /**
     * 目的网点编码
     */
    private String endNodeCode;

    /**
     * 目的网点类型
     */
    private String endNodeType;


    public String getBeginNodeCode() {
        return beginNodeCode;
    }

    public void setBeginNodeCode(String beginNodeCode) {
        this.beginNodeCode = beginNodeCode;
    }

    public String getStartNodeType() {
        return startNodeType;
    }

    public void setStartNodeType(String startNodeType) {
        this.startNodeType = startNodeType;
    }

    public String getEndNodeCode() {
        return endNodeCode;
    }

    public void setEndNodeCode(String endNodeCode) {
        this.endNodeCode = endNodeCode;
    }

    public String getEndNodeType() {
        return endNodeType;
    }

    public void setEndNodeType(String endNodeType) {
        this.endNodeType = endNodeType;
    }
}
