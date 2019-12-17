package com.jd.bluedragon.common.dto.air.response;

import java.io.Serializable;

/**
 * 航班始末机场
 * @author : xumigen
 * @date : 2019/11/4
 */
public class AirPortResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String beginNodeCode;

    private String beginNodeName;

    private String endNodeCode;

    private String endNodeName;

    public String getBeginNodeCode() {
        return beginNodeCode;
    }

    public void setBeginNodeCode(String beginNodeCode) {
        this.beginNodeCode = beginNodeCode;
    }

    public String getBeginNodeName() {
        return beginNodeName;
    }

    public void setBeginNodeName(String beginNodeName) {
        this.beginNodeName = beginNodeName;
    }

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
}
