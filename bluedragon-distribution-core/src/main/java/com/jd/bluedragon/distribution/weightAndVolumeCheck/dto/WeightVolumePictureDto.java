package com.jd.bluedragon.distribution.weightAndVolumeCheck.dto;

import java.io.Serializable;

/**
 * 抽检图片结果
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-08-12 12:42:16 周四
 */
public class WeightVolumePictureDto implements Serializable {

    private String waybillCode;

    private String packageCode;

    private String url;

    public String getWaybillCode() {
        return waybillCode;
    }

    public WeightVolumePictureDto setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
        return this;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public WeightVolumePictureDto setPackageCode(String packageCode) {
        this.packageCode = packageCode;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public WeightVolumePictureDto setUrl(String url) {
        this.url = url;
        return this;
    }
}
