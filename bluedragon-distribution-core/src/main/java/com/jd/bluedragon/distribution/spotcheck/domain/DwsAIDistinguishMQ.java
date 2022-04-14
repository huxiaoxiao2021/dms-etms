package com.jd.bluedragon.distribution.spotcheck.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 设备抽检图片AI识别下发MQ实体
 *
 * @author hujiping
 * @date 2022/3/17 3:58 PM
 */
public class DwsAIDistinguishMQ implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uuid;
    private String waybillCode;
    private Integer siteCode;
    private List<Package> packages;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public List<Package> getPackages() {
        return packages;
    }

    public void setPackages(List<Package> packages) {
        this.packages = packages;
    }

    public static class Package {
        private String packageCode;
        private String picUrl;

        public String getPackageCode() {
            return packageCode;
        }

        public void setPackageCode(String packageCode) {
            this.packageCode = packageCode;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }
    }
}
