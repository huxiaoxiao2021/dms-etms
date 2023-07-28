package com.jd.bluedragon.common.dto.jyexpection.request;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/26 20:08
 * @Description: 异常类型校验
 */
public class ExpTypeCheckReq extends ExpBaseReq{

    /**
     * 异常包裹号
     */
    private String barCode;

    /**
     * 异常类型 1：报废   2：破损
     */
    private Integer type;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
