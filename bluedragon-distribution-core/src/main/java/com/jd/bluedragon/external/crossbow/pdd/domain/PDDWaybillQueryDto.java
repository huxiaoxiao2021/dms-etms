package com.jd.bluedragon.external.crossbow.pdd.domain;

/**
 * <p>
 *     拼多多的电子面单号查询对象
 *
 * @author wuzuxiang
 * @since 2019/10/15
 **/
public class PDDWaybillQueryDto {

    /**
     * 电子面单号
     */
    private String waybillCode;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }
}
