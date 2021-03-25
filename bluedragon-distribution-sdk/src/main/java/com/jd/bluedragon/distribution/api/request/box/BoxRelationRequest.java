package com.jd.bluedragon.distribution.api.request.box;

import com.jd.bluedragon.distribution.api.JdRequest;

import java.io.Serializable;

/**
 * @ClassName BoxRelationRequest
 * @Description
 * @Author wyh
 * @Date 2020/12/14 18:36
 **/
public class BoxRelationRequest extends JdRequest implements Serializable {

    private static final long serialVersionUID = 7384151956726384221L;

    private String userErp;

    /**
     * 箱号
     */
    private String boxCode;

    /**
     * 关联箱号
     */
    private String relationBoxCode;

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

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
