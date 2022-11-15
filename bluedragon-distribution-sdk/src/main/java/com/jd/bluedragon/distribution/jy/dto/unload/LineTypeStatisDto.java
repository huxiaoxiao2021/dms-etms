package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;


public class LineTypeStatisDto implements Serializable {

    private static final long serialVersionUID = 8148137487331081445L;
    /**
     * 线路类型
     */
    private Integer lineType;

    /**
     * 线路类型描述
     */
    private String lineTypeName;

    /**
     * 该类型下任务数量
     */
    private Integer count;

    public Integer getLineType() {
        return lineType;
    }

    public void setLineType(Integer lineType) {
        this.lineType = lineType;
    }

    public String getLineTypeName() {
        return lineTypeName;
    }

    public void setLineTypeName(String lineTypeName) {
        this.lineTypeName = lineTypeName;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
