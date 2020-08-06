package com.jd.bluedragon.distribution.weightAndVolumeCheck;

import java.io.Serializable;
import java.util.List;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2019/10/31 20:00
 */
public class SpotCheckData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 运单号
     * */
    private String waybillCode;
    /**
     * 抽检类型 1:运单 2:包裹
     * */
    private Integer isWaybillSpotCheck;
    /**
     * 运单总重量
     * */
    private Double totalWeight;
    /**
     * 运单总体积
     * */
    private Double totalVolume;
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
     * 包裹数据
     * */
    private List<packageData> packageDataList;
    /**
     * 抽检来源
     * @see SpotCheckSourceEnum
     * */
    private String fromSource;

    public String getFromSource() {
        return fromSource;
    }

    public void setFromSource(String fromSource) {
        this.fromSource = fromSource;
    }

    public Integer getIsWaybillSpotCheck() {
        return isWaybillSpotCheck;
    }

    public void setIsWaybillSpotCheck(Integer isWaybillSpotCheck) {
        this.isWaybillSpotCheck = isWaybillSpotCheck;
    }

    public List<packageData> getPackageDataList() {
        return packageDataList;
    }

    public void setPackageDataList(List<packageData> packageDataList) {
        this.packageDataList = packageDataList;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public Double getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(Double totalVolume) {
        this.totalVolume = totalVolume;
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

    public Integer getIsExcess() {
        return isExcess;
    }

    public void setIsExcess(Integer isExcess) {
        this.isExcess = isExcess;
    }

    public static class packageData {
        /*
        * 包裹号
        * */
        private String packageCode;
        /*
         * 包裹重量
         * */
        private Double weight;
        /*
         * 包裹长
         * */
        private Double length;
        /*
         * 包裹宽
         * */
        private Double width;
        /*
         * 包过高
         * */
        private Double height;

        public String getPackageCode() {
            return packageCode;
        }

        public void setPackageCode(String packageCode) {
            this.packageCode = packageCode;
        }

        public Double getWeight() {
            return weight;
        }

        public void setWeight(Double weight) {
            this.weight = weight;
        }

        public Double getLength() {
            return length;
        }

        public void setLength(Double length) {
            this.length = length;
        }

        public Double getWidth() {
            return width;
        }

        public void setWidth(Double width) {
            this.width = width;
        }

        public Double getHeight() {
            return height;
        }

        public void setHeight(Double height) {
            this.height = height;
        }
    }


}
