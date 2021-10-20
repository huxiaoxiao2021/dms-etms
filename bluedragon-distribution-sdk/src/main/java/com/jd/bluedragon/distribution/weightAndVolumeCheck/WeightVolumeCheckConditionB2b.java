package com.jd.bluedragon.distribution.weightAndVolumeCheck;

import java.io.Serializable;
import java.util.List;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2019/10/10 16:15
 */
public class WeightVolumeCheckConditionB2b implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否是运单
     * */
    private Integer isWaybill;
    /**
     * 运单号/包裹号
     * */
    private String waybillOrPackageCode;
    /**
     * 包裹数量
     * */
    private Integer packNum;
    /**
     * 运单总重量
     * */
    private Double waybillWeight;
    /**
     * 运单总体积
     * */
    private Double waybillVolume;
    /**
     * 上传图片数量
     * */
    private Integer upLoadNum;
    /**
     * 操作站点
     * */
    private Integer createSiteCode;
    /**
     * 操作人ERP
     * */
    private String loginErp;
    /**
     * 是否超标
     * */
    private Integer isExcess;

    /**
     * PDA来源标识
     */
    private Integer pdaSource;

    /**
     * 图片urls
     */
    private List<String> urls;

    /**
     * 是否安卓抽检：false表示web页面抽检，true表示安卓抽检
     */
    private boolean androidSpotCheck = false;

    public String getLoginErp() {
        return loginErp;
    }

    public void setLoginErp(String loginErp) {
        this.loginErp = loginErp;
    }

    public Integer getIsWaybill() {
        return isWaybill;
    }

    public void setIsWaybill(Integer isWaybill) {
        this.isWaybill = isWaybill;
    }

    public String getWaybillOrPackageCode() {
        return waybillOrPackageCode;
    }

    public void setWaybillOrPackageCode(String waybillOrPackageCode) {
        this.waybillOrPackageCode = waybillOrPackageCode;
    }

    public Integer getPackNum() {
        return packNum;
    }

    public void setPackNum(Integer packNum) {
        this.packNum = packNum;
    }

    public Double getWaybillWeight() {
        return waybillWeight;
    }

    public void setWaybillWeight(Double waybillWeight) {
        this.waybillWeight = waybillWeight;
    }

    public Double getWaybillVolume() {
        return waybillVolume;
    }

    public void setWaybillVolume(Double waybillVolume) {
        this.waybillVolume = waybillVolume;
    }

    public Integer getUpLoadNum() {
        return upLoadNum;
    }

    public void setUpLoadNum(Integer upLoadNum) {
        this.upLoadNum = upLoadNum;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public Integer getIsExcess() {
        return isExcess;
    }

    public void setIsExcess(Integer isExcess) {
        this.isExcess = isExcess;
    }

    public Integer getPdaSource() {
        return pdaSource;
    }

    public void setPdaSource(Integer pdaSource) {
        this.pdaSource = pdaSource;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public boolean getAndroidSpotCheck() {
        return androidSpotCheck;
    }

    public void setAndroidSpotCheck(boolean androidSpotCheck) {
        this.androidSpotCheck = androidSpotCheck;
    }
}
