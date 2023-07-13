package com.jd.bluedragon.common.dto.comboard.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import java.io.Serializable;

public class FindGoodsReq extends BaseReq implements Serializable {
    /**
     * 扫描单号
     */
    private String barCode;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
}
