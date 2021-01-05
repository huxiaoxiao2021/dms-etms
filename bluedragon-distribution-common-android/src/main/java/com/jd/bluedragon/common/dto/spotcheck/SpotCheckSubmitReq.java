package com.jd.bluedragon.common.dto.spotcheck;

import java.io.Serializable;
import java.util.List;

/**
 * @program: bluedragon-distribution
 * @description: 抽检提交接口
 * @author: wuming
 * @create: 2020-12-30 15:51
 */
public class SpotCheckSubmitReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操作人erp
     */
    private String loginErp;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 操作站点编号
     */
    private Integer createSiteCode;

    /**
     * 复核重量
     */
    private Double weight;

    /**
     * 复核体积
     */
    private Double volume;

    /**
     * 抽检图片
     */
    private List<String> urls;

    private Integer source;

    private Integer isExcess;

    public SpotCheckSubmitReq() {
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

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
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

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Integer getIsExcess() {
        return isExcess;
    }

    public void setIsExcess(Integer isExcess) {
        this.isExcess = isExcess;
    }
}
