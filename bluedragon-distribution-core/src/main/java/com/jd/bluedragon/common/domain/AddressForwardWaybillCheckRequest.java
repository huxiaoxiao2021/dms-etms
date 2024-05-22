package com.jd.bluedragon.common.domain;

import com.jd.etms.waybill.dto.WaybillVasDto;

import java.io.Serializable;
import java.util.List;

/**
 * 改址转寄运单请求参数
 */
public class AddressForwardWaybillCheckRequest implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 补打标识
     */
    private String waybillSign;

    /**
     * 换单打印标识
     */
    private String omcOrderCode;

    /**
     * 运单增值服务
     */
    private List<WaybillVasDto> waybillVasDtos;

    public String getWaybillSign() {
        return waybillSign;
    }

    public void setWaybillSign(String waybillSign) {
        this.waybillSign = waybillSign;
    }

    public String getOmcOrderCode() {
        return omcOrderCode;
    }

    public void setOmcOrderCode(String omcOrderCode) {
        this.omcOrderCode = omcOrderCode;
    }

    public List<WaybillVasDto> getWaybillVasDtos() {
        return waybillVasDtos;
    }

    public void setWaybillVasDtos(List<WaybillVasDto> waybillVasDtos) {
        this.waybillVasDtos = waybillVasDtos;
    }
}
