package com.jd.bluedragon.distribution.external.pdd.domain;

import com.jd.bluedragon.distribution.external.domain.DMSExternalDto;

/**
 * <p>
 *     拼多多的面单打印接口信息的请求参数
 *
 * @author wuzuxiang
 * @since 2019/10/18
 **/
public class PDDWaybillPrintInfoRequest extends DMSExternalDto {

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
