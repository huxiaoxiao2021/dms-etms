package com.jd.bluedragon.common.dto.operation.workbench.unseal.response;

import java.io.Serializable;

/**
 * @ClassName LineTypeStatis
 * @Description
 * @Author wyh
 * @Date 2022/3/2 19:32
 **/
public class LineTypeStatis implements Serializable {

    private static final long serialVersionUID = -7139998986734309410L;

    /**
     * 线路类型
     */
    private Integer lineType;

    /**
     * 线路类型描述
     */
    private String lineTypeName;

    /**
     * 类型下车辆总数
     */
    private Long total;

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

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
