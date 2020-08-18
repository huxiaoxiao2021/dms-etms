package com.jd.bluedragon.distribution.sorting.domain;

/**
 * 分拣验证对象
 * Created by wangtingwei on 2015/3/16.
 */
public class SortingCheck {
        /**
         * 操作类型
         */
        Integer operateType = 1;

        /**
         * 包裹号
         */
        String packageCode;
        /**
         * 分拣中心编码
         */
        Integer createSiteCode;
        /**
         * 分拣中心名称
         */
        String createSiteName;
        /**
         * 操作人编码
         */
        Integer operateUserCode;
        /**
         * 操作人名称
         */
        String operateUserName;
        /**
         * 操作时间
         */
        String operateTime;
        /**
         * 收货站点
         */
        Integer receiveSiteCode;
        /**
         * 业务类型
         */
        Integer businessType;
        /**
         * 箱号
         */
        String boxCode;

        /**
         * 是否报丢 1报丢
         */
        Integer isLoss;

        public Integer getOperateType() {
            return operateType;
        }
        public void setOperateType(Integer operateType) {
            this.operateType = operateType;
        }
        public Integer getReceiveSiteCode() {
            return receiveSiteCode;
        }
        public void setReceiveSiteCode(Integer receiveSiteCode) {
            this.receiveSiteCode = receiveSiteCode;
        }
        public Integer getBusinessType() {
            return businessType;
        }
        public void setBusinessType(Integer businessType) {
            this.businessType = businessType;
        }
        public String getBoxCode() {
            return boxCode;
        }
        public void setBoxCode(String boxCode) {
            this.boxCode = boxCode;
        }
        public String getPackageCode() {
            return packageCode;
        }
        public void setPackageCode(String packageCode) {
            this.packageCode = packageCode;
        }
        public Integer getCreateSiteCode() {
            return createSiteCode;
        }
        public void setCreateSiteCode(Integer createSiteCode) {
            this.createSiteCode = createSiteCode;
        }
        public String getCreateSiteName() {
            return createSiteName;
        }
        public void setCreateSiteName(String createSiteName) {
            this.createSiteName = createSiteName;
        }
        public Integer getOperateUserCode() {
            return operateUserCode;
        }
        public void setOperateUserCode(Integer operateUserCode) {
            this.operateUserCode = operateUserCode;
        }
        public String getOperateUserName() {
            return operateUserName;
        }
        public void setOperateUserName(String operateUserName) {
            this.operateUserName = operateUserName;
        }
        public String getOperateTime() {
            return operateTime;
        }
        public void setOperateTime(String operateTime) {
            this.operateTime = operateTime;
        }

        public Integer getIsLoss() {
            return isLoss;
        }

        public void setIsLoss(Integer isLoss) {
            this.isLoss = isLoss;
        }
}
