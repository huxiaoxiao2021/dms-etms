package com.jd.bluedragon.common.dto.carTask.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * 运输车辆任务查询
 */
public class FindEndNodeRequest extends BaseReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 扫描条码内容
     */
    private String barCode;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
}
