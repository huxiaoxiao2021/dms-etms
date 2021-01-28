package com.jd.bluedragon.common.dto.spotcheck;

import java.io.Serializable;

/**
 * @program: bluedragon-distribution
 * @description: 是否超标请求参数
 * @author: wuming
 * @create: 2020-12-28 14:34
 */
public class SpotCheckCheckReq implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 复核重量
     */
    private Double weight;

    /**
     * 复核体积
     */
    private Double volume;

    /**
     * 操作站点编号
     */
    private Integer createSiteCode;

    /**
     * 操作人erp
     */
    private String loginErp;

    /**
     * 运单号
     */
    private String waybillCode;

    public SpotCheckCheckReq() {
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getLoginErp() {
        return loginErp;
    }

    public void setLoginErp(String loginErp) {
        this.loginErp = loginErp;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }
}
