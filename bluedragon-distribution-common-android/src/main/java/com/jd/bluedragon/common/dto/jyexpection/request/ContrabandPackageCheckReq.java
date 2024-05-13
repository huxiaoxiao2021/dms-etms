package com.jd.bluedragon.common.dto.jyexpection.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

/**
 * @Author liwenji3
 * @Date 2024/5/11 15:58
 * @Description 违禁品包裹校验
 */
public class ContrabandPackageCheckReq extends BaseReq {

    /**
     * 包裹号
     */
    private String barCode;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
}
