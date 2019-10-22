package com.jd.bluedragon.distribution.send.domain.dto;

import com.jd.ql.dms.common.web.mvc.api.PagerCondition;

import java.util.Date;

/**
 * @author : xumigen
 * @date : 2019/5/13
 */
public class SendDetailDto implements PagerCondition{

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

    /**
     * 时间范围 - 开始时间
     */
    private Date startTime;

    /**
     * 时间范围 - 结束时间
     */
    private Date endTime;

    private Integer offset;

    private Integer limit;

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public int getLimit() {
        return limit;
    }

    public Long getSendDId() {
        return sendDId;
    }

    public void setSendDId(Long sendDId) {
        this.sendDId = sendDId;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getPackageBarcode() {
        return packageBarcode;
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
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPickupCode() {
        return pickupCode;
    }

    public void setPickupCode(String pickupCode) {
        this.pickupCode = pickupCode;
    }

    public Integer getSendType() {
        return sendType;
    }

    public void setSendType(Integer sendType) {
        this.sendType = sendType;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Integer getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(Integer isCancel) {
        this.isCancel = isCancel;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getExcuteCount() {
        return excuteCount;
    }

    public void setExcuteCount(Integer excuteCount) {
        this.excuteCount = excuteCount;
    }

    public Date getExcuteTime() {
        return excuteTime;
    }

    public void setExcuteTime(Date excuteTime) {
        this.excuteTime = excuteTime;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public String getSpareReason() {
        return spareReason;
    }

    public void setSpareReason(String spareReason) {
        this.spareReason = spareReason;
    }

    public String getSpareTranCode() {
        return spareTranCode;
    }

    public void setSpareTranCode(String spareTranCode) {
        this.spareTranCode = spareTranCode;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
