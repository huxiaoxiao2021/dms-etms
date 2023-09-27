package com.jd.bluedragon.distribution.send.domain;

import com.jd.bluedragon.distribution.api.domain.OperatorData;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.converter.BeanConverter;

import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.List;

public class SendDetail implements java.io.Serializable, Comparable<SendDetail> {

    private static final long serialVersionUID = 2292780849533744283L;

    /**
     * 全局唯一ID
     */
    private Long sendDId;

    /**
     * 发货交接单号
     */
    private String sendCode;

    /**
     * 箱号
     */
    private String boxCode;

    /**
     * 包裹号
     */
    private String packageBarcode;

    /**
     * 包裹数量
     */
    private Integer packageNum;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 运单号
     */
    private String pickupCode;

    /**
     * 发货交接单类型(10 退货 20 转站 30 第三方)
     */
    private Integer sendType;

    /**
     * 操作单位编码
     */
    private Integer createSiteCode;

    /**
     * 操作单位编码
     */
    private Integer receiveSiteCode;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 操作人
     */
    private String createUser;

    /**
     * 操作人编码
     */
    private Integer createUserCode;

    /**
     * 回传状态【】
     */
    private Integer status;

    /**
     * 是否取消状态
     */
    private Integer isCancel;

    /**
     * 重量
     */
    private Double weight;

    /**
     * 最后修改时间
     */
    private Date updateTime;

    /**
     * 执行次数
     */
    private Integer excuteCount;

    /**
     * 执行时间
     */
    private Date excuteTime;

    /**
     * 是否删除 '0' 删除 '1' 使用
     */
    private Integer yn;

    /**
     * 备件库退货原因
     */
    private String spareReason;
    /**
     * 备件库入库单号
     */
    private String spareTranCode;

    /**
     * 逆向物流接入报损系统 0正常分拣 1  报丢分拣
     */
    private Integer isLoss;

    /**
     * 逆向标识字段 1：报损 2：三方七折退备件库，以后取代isLoss
     */
    private Integer featureType;

    /**
     * 亚一返仓标识 b1 c1
     */
    private Integer whReverse;

    private String source;

    /**
     * 组板板号
     */
    private String boardCode;

    /**
     * 发货业务来源
     */
    private Integer bizSource;
    private List<String> boxCodeList;
    /**
     *@see com.jd.bluedragon.distribution.api.enums.OperatorTypeEnum
     * 操作者类型编码
     */
	private Integer operatorTypeCode;
    /**
     * 操作者id
     */
	private String operatorId;

    /**
     * 操作场地-站点（分拣）类型
     */
    private Integer createSiteType;

    /**
     * 操作场地-站点（分拣）子站点类型
     */
    private Integer createSubType;

    /**
     * 操作场地-站点（分拣）三级类型
     */
    private Integer createThirdType;

    /**
     * 操作场地-分拣中心类型
     */
    private Integer createSortType;

    /**
     * 操作场地-分拣中心子类型
     */
    private Integer createSortSubType;

    /**
     * 操作场地-分拣中心三级类型
     */
    private Integer createSortThirdType;

    /**
     * 目的场地-站点（分拣）类型
     */
    private Integer receiveSiteType;

    /**
     * 目的场地-站点（分拣）子站点类型
     */
    private Integer receiveSubType;

    /**
     * 目的场地-站点（分拣）三级类型
     */
    private Integer receiveThirdType;

    /**
     * 目的场地-分拣中心类型
     */
    private Integer receiveSortType;

    /**
     * 目的场地-分拣中心子类型
     */
    private Integer receiveSortSubType;

    /**
     * 目的场地-分拣中心三级类型
     */
    private Integer receiveSortThirdType;
    /**
     * 操作信息对象
     */
	private OperatorData operatorData;

	
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSpareTranCode() {
        return spareTranCode;
    }

    public void setSpareTranCode(String spareTranCode) {
        this.spareTranCode = spareTranCode;
    }

    public SendDetail() {
        super();
    }

    public Long getSendDId() {
        return this.sendDId;
    }

    public void setSendDId(Long sendDId) {
        this.sendDId = sendDId;
    }

    public String getSendCode() {
        return this.sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getBoxCode() {
        return this.boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getPackageBarcode() {
        return this.packageBarcode;
    }

    public void setPackageBarcode(String packageBarcode) {
        this.packageBarcode = packageBarcode;
    }

    public Integer getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(Integer packageNum) {
        this.packageNum = packageNum;
    }

    public String getWaybillCode() {
        return this.waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getCreateSiteCode() {
        return this.createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateUser() {
        return this.createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Integer getCreateUserCode() {
        return this.createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getUpdateTime() {
        return this.updateTime == null ? null : (Date) this.updateTime.clone();
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime == null ? null : (Date) updateTime.clone();
    }

    public Integer getYn() {
        return this.yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Integer getSendType() {
        return this.sendType;
    }

    public void setSendType(Integer sendType) {
        this.sendType = sendType;
    }

    public Integer getReceiveSiteCode() {
        return this.receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public Integer getIsCancel() {
        return this.isCancel;
    }

    public void setIsCancel(Integer isCancel) {
        this.isCancel = isCancel;
    }

    public Double getWeight() {
        return this.weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Date getOperateTime() {
        return this.operateTime == null ? null : (Date) this.operateTime.clone();
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime == null ? null : (Date) operateTime.clone();
    }

    public Date getCreateTime() {
        return this.createTime == null ? null : (Date) this.createTime.clone();
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime == null ? null : (Date) createTime.clone();
    }

    public String getPickupCode() {
        return this.pickupCode;
    }

    public void setPickupCode(String pickupCode) {
        this.pickupCode = pickupCode;
    }


    public Integer getExcuteCount() {
        return excuteCount;
    }

    public void setExcuteCount(Integer excuteCount) {
        this.excuteCount = excuteCount;
    }

    public Date getExcuteTime() {
        return this.excuteTime == null ? null : (Date) this.excuteTime.clone();
    }

    public void setExcuteTime(Date excuteTime) {
        this.excuteTime = excuteTime == null ? null : (Date) excuteTime.clone();
    }

    public String getSpareReason() {
        return spareReason;
    }

    public void setSpareReason(String spareReason) {
        this.spareReason = spareReason;
    }

    public Integer getIsLoss() {
        return isLoss;
    }

    public void setIsLoss(Integer isLoss) {
        this.isLoss = isLoss;
    }

    public Integer getFeatureType() {
        return featureType;
    }

    public void setFeatureType(Integer featureType) {
        this.featureType = featureType;
    }

    public Integer getWhReverse() {
        return whReverse;
    }

    public void setWhReverse(Integer whReverse) {
        this.whReverse = whReverse;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public Integer getBizSource() {
        return bizSource;
    }

    public void setBizSource(Integer bizSource) {
        this.bizSource = bizSource;
    }

    public List<String> getBoxCodeList() {
        return boxCodeList;
    }

    public void setBoxCodeList(List<String> boxCodeList) {
        this.boxCodeList = boxCodeList;
    }

    @Override
    public String toString() {
        return "SendDatail [sendDId=" + sendDId + ", sendCode=" + sendCode
                + ", boxCode=" + boxCode + ", packageBarcode=" + packageBarcode
                + ", waybillCode=" + waybillCode + ", pickupCode=" + pickupCode
                + ", sendType=" + sendType
                + ", createSiteCode=" + createSiteCode + ", receiveSiteCode="
                + receiveSiteCode + ", createTime=" + createTime
                + ", createUser=" + createUser + ", createUserCode="
                + createUserCode + ", status=" + status + ", isCancel="
                + isCancel + ", weight=" + weight + ", updateTime="
                + updateTime + ", excuteCount=" + excuteCount + ", excuteTime="
                + excuteTime + ", yn=" + yn + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.waybillCode == null ? 0 : this.waybillCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        SendDetail other = (SendDetail) obj;
        if (this.waybillCode == null) {
            if (other.waybillCode != null) {
                return false;
            }
            if (other.waybillCode == null && other.packageBarcode != null) {
                return false;
            }
        } else if (!this.waybillCode.equals(other.waybillCode)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(SendDetail sendDetail) {
        if (null == sendDetail || StringUtils.isBlank(sendDetail.getPackageBarcode()) || StringUtils.isBlank(this.getPackageBarcode())) {
            return 0;
        } else {
            return this.getPackageBarcode().compareTo(sendDetail.getPackageBarcode());
        }
    }

    public static SendDetail toSendDatail(Sorting sorting) {
        SendDetail sendDetail = new SendDetail();
        
        OperatorData operatorData = BeanConverter.convertToOperatorData(sorting);
        sendDetail.setOperatorTypeCode(operatorData.getOperatorTypeCode());
        sendDetail.setOperatorId(operatorData.getOperatorId());
        sendDetail.setOperatorData(operatorData);  
        
        String aBoxCode = sorting.getBoxCode();
        String aPackageCode = sorting.getPackageCode();

        sendDetail.setCreateSiteCode(sorting.getCreateSiteCode());
        sendDetail.setReceiveSiteCode(sorting.getReceiveSiteCode());
        sendDetail.setCreateUser(sorting.getCreateUser());
        sendDetail.setCreateUserCode(sorting.getCreateUserCode());
        sendDetail.setCreateUserCode(sorting.getCreateUserCode());
        sendDetail.setIsCancel(sorting.getIsCancel());
        sendDetail.setSendType(sorting.getType());
        sendDetail.setCreateTime(new Date());
        sendDetail.setOperateTime(sorting.getOperateTime());
        sendDetail.setUpdateTime(sorting.getUpdateTime());
        sendDetail.setBoxCode(StringHelper.isNotEmpty(aBoxCode) ? aBoxCode : aPackageCode);
        sendDetail.setIsLoss(sorting.getIsLoss());
        sendDetail.setFeatureType(sorting.getFeatureType());
        sendDetail.setWhReverse(sorting.getWhReverse());

        if (WaybillUtil.isReverseSpare(sorting.getType(), aPackageCode)) {
            sendDetail.setSpareReason(sorting.getSpareReason());
            sendDetail.setWaybillCode(sorting.getWaybillCode());
            sendDetail.setPackageBarcode(aPackageCode);
        } else {
            if (!WaybillUtil.isSurfaceCode(aPackageCode)) {
                sendDetail.setWaybillCode(WaybillUtil.getWaybillCode(aPackageCode));
            }

            if (WaybillUtil.isPackageCode(aPackageCode)) {
                sendDetail.setPackageBarcode(aPackageCode);
            }

            if (WaybillUtil.isWaybillCode(aPackageCode)) {
                sendDetail.setWaybillCode(WaybillUtil.getWaybillCode(aPackageCode));
            }
        }
        return sendDetail;
    }

    public static class Builder {
        /*Required parameters*/
        /*包裹号*/
        private String packageBarcode;

        /*操作单位Code*/
        private Integer createSiteCode;

        /*Optional parameters*/
        /*收货单位Code*/
        private Integer receiveSiteCode;

        /*箱号*/
        private String boxCode;

        /**
         * 发货交接单类型(10 正向 2逆向 30 第三方)
         */
        private Integer sendType;

        /*更新时间*/
        private Date updateTime;

        /**
         * 操作人
         */
        private String createUser;

        /**
         * 操作人编码
         */
        private Integer createUserCode;

        /**
         * 运单号
         */
        private String waybillCode;

        /**
         * 操作时间
         */
        private Date operateTime;

        /**
         * 创建时间
         */
        private Date createTime;

        /**
         * 是否取消状态
         */
        private Integer isCancel;

        public Builder(String packageBarcode, Integer createSiteCode) {
            super();
            this.packageBarcode = packageBarcode;
            this.createSiteCode = createSiteCode;
        }

        public Builder receiveSiteCode(Integer val) {
            receiveSiteCode = val;
            return this;
        }

        public Builder boxCode(String val) {
            boxCode = val;
            return this;
        }

        public Builder updateTime(Date val) {
            updateTime = val != null ? (Date) val.clone() : null;
            return this;
        }

        public Builder sendType(Integer val) {
            sendType = val;
            return this;
        }

        public Builder createUser(String val) {
            createUser = val;
            return this;
        }

        public Builder createUserCode(Integer val) {
            createUserCode = val;
            return this;
        }

        public Builder waybillCode(String val) {
            waybillCode = val;
            return this;
        }

        public Builder createTime(Date val) {
            createTime = val != null ? (Date) val.clone() : null;
            return this;
        }

        public Builder operateTime(Date val) {
            operateTime = val != null ? (Date) val.clone() : null;
            return this;
        }

        public Builder isCancel(Integer val) {
            isCancel = val;
            return this;
        }

        public SendDetail build() {
            return new SendDetail(this);
        }
    }

    public SendDetail(Builder builder) {
        this.packageBarcode = builder.packageBarcode;
        this.createSiteCode = builder.createSiteCode;
        this.boxCode = builder.boxCode;
        this.receiveSiteCode = builder.receiveSiteCode;
        this.updateTime = builder.updateTime;
        this.sendType = builder.sendType;
        this.createUser = builder.createUser;
        this.createUserCode = builder.createUserCode;
        this.waybillCode = builder.waybillCode;
        this.createTime = builder.createTime;
        this.isCancel = builder.isCancel;
        this.operateTime = builder.operateTime;
    }

	public Integer getOperatorTypeCode() {
		return operatorTypeCode;
	}

	public void setOperatorTypeCode(Integer operatorTypeCode) {
		this.operatorTypeCode = operatorTypeCode;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

    public Integer getCreateSiteType() {
        return createSiteType;
    }

    public void setCreateSiteType(Integer createSiteType) {
        this.createSiteType = createSiteType;
    }

    public Integer getCreateSubType() {
        return createSubType;
    }

    public void setCreateSubType(Integer createSubType) {
        this.createSubType = createSubType;
    }

    public Integer getCreateThirdType() {
        return createThirdType;
    }

    public void setCreateThirdType(Integer createThirdType) {
        this.createThirdType = createThirdType;
    }

    public Integer getCreateSortType() {
        return createSortType;
    }

    public void setCreateSortType(Integer createSortType) {
        this.createSortType = createSortType;
    }

    public Integer getCreateSortSubType() {
        return createSortSubType;
    }

    public void setCreateSortSubType(Integer createSortSubType) {
        this.createSortSubType = createSortSubType;
    }

    public Integer getCreateSortThirdType() {
        return createSortThirdType;
    }

    public void setCreateSortThirdType(Integer createSortThirdType) {
        this.createSortThirdType = createSortThirdType;
    }

    public Integer getReceiveSiteType() {
        return receiveSiteType;
    }

    public void setReceiveSiteType(Integer receiveSiteType) {
        this.receiveSiteType = receiveSiteType;
    }

    public Integer getReceiveSubType() {
        return receiveSubType;
    }

    public void setReceiveSubType(Integer receiveSubType) {
        this.receiveSubType = receiveSubType;
    }

    public Integer getReceiveThirdType() {
        return receiveThirdType;
    }

    public void setReceiveThirdType(Integer receiveThirdType) {
        this.receiveThirdType = receiveThirdType;
    }

    public Integer getReceiveSortType() {
        return receiveSortType;
    }

    public void setReceiveSortType(Integer receiveSortType) {
        this.receiveSortType = receiveSortType;
    }

    public Integer getReceiveSortSubType() {
        return receiveSortSubType;
    }

    public void setReceiveSortSubType(Integer receiveSortSubType) {
        this.receiveSortSubType = receiveSortSubType;
    }

    public Integer getReceiveSortThirdType() {
        return receiveSortThirdType;
    }

    public void setReceiveSortThirdType(Integer receiveSortThirdType) {
        this.receiveSortThirdType = receiveSortThirdType;
    }

	public OperatorData getOperatorData() {
		return operatorData;
	}

	public void setOperatorData(OperatorData operatorData) {
		this.operatorData = operatorData;
	}
}