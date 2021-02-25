package com.jd.bluedragon.common.dto.box.response;

import java.io.Serializable;

/**
 * @ClassName BoxRelationDto
 * @Description
 * @Author wyh
 * @Date 2020/12/22 21:09
 **/
public class BoxRelationDto implements Serializable {

    private static final long serialVersionUID = 3581313941909467235L;

    /**
     * 箱号
     */
    private String boxCode;

    /**
     * 关联箱号
     */
    private String relationBoxCode;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getRelationBoxCode() {
        return relationBoxCode;
    }

    public void setRelationBoxCode(String relationBoxCode) {
        this.relationBoxCode = relationBoxCode;
    }
}
