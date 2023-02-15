package com.jd.bluedragon.common.dto.comboard.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class CTTGroupDataReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -704246219827299271L;
    /**
     * true 查询本岗位的 false 查询本场地的
     */
    private boolean groupQueryFlag;

    public boolean isGroupQueryFlag() {
        return groupQueryFlag;
    }

    public void setGroupQueryFlag(boolean groupQueryFlag) {
        this.groupQueryFlag = groupQueryFlag;
    }
}
