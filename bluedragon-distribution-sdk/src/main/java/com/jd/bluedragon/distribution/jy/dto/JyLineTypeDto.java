package com.jd.bluedragon.distribution.jy.dto;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/
public class JyLineTypeDto implements Serializable {
    private static final long serialVersionUID = 8148137487331081445L;
    private Integer lineType;
    private String lineTypeName;
    private Integer total;

    public JyLineTypeDto() {
    }

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

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
