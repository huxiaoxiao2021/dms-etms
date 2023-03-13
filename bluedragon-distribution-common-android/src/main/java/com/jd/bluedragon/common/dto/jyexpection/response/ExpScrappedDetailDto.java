package com.jd.bluedragon.common.dto.jyexpection.response;

import com.jd.bluedragon.common.dto.jyexpection.request.ExpBaseReq;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/3/10 16:29
 * @Description: 报废
 */
public class ExpScrappedDetailDto implements Serializable {

    // 业务主键id
    private String bizId;

    // 用户ERP
    private String userErp;

    // 站点id
    private Integer siteId;

    // 岗位码
    private String positionCode;

    // 存储类型 0暂存 1提交
    private String saveType;

    /**
     * 报废类型code 值
     */
    private Integer scrappedTypCode;

    /**
     * 物品照片 多个逗号分割
     */
    private String goodsImageUrl;

    /**
     * 证明照片 多个逗号分割
     */
    private String certifyImageUrl;

    /**
     * 审核第一人erp
     */
    private String firstChecker;

    /**
     * 第一人审核状态：1：待审批 2：审批通过 3：审批驳回
     */
    private Integer firstCheckStatus;

    /**
     * 第一审批人审批时间
     */
    private Date firstCheckTime;

    /**
     * 审核第二人erp
     */
    private String secondChecker;

    /**
     * 第二人审核状态：1：待审批 2：审批通过 3：审批驳回
     */
    private Integer secondCheckStatus;

    /**
     * 第二审批人审批时间
     */
    private Date secondCheckTime;

    /**
     * 审核第三人erp
     */
    private String thirdChecker;

    /**
     * 第三人审核状态：1：待审批 2：审批通过 3：审批驳回
     */
    private Integer thirdCheckStatus;

    /**
     * 第三审批人审批时间
     */
    private Date thirdCheckTime;


    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getSaveType() {
        return saveType;
    }

    public void setSaveType(String saveType) {
        this.saveType = saveType;
    }

    public Integer getScrappedTypCode() {
        return scrappedTypCode;
    }

    public void setScrappedTypCode(Integer scrappedTypCode) {
        this.scrappedTypCode = scrappedTypCode;
    }

    public String getGoodsImageUrl() {
        return goodsImageUrl;
    }

    public void setGoodsImageUrl(String goodsImageUrl) {
        this.goodsImageUrl = goodsImageUrl;
    }

    public String getCertifyImageUrl() {
        return certifyImageUrl;
    }

    public void setCertifyImageUrl(String certifyImageUrl) {
        this.certifyImageUrl = certifyImageUrl;
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public String getFirstChecker() {
        return firstChecker;
    }

    public void setFirstChecker(String firstChecker) {
        this.firstChecker = firstChecker;
    }

    public Integer getFirstCheckStatus() {
        return firstCheckStatus;
    }

    public void setFirstCheckStatus(Integer firstCheckStatus) {
        this.firstCheckStatus = firstCheckStatus;
    }

    public Date getFirstCheckTime() {
        return firstCheckTime;
    }

    public void setFirstCheckTime(Date firstCheckTime) {
        this.firstCheckTime = firstCheckTime;
    }

    public String getSecondChecker() {
        return secondChecker;
    }

    public void setSecondChecker(String secondChecker) {
        this.secondChecker = secondChecker;
    }

    public Integer getSecondCheckStatus() {
        return secondCheckStatus;
    }

    public void setSecondCheckStatus(Integer secondCheckStatus) {
        this.secondCheckStatus = secondCheckStatus;
    }

    public Date getSecondCheckTime() {
        return secondCheckTime;
    }

    public void setSecondCheckTime(Date secondCheckTime) {
        this.secondCheckTime = secondCheckTime;
    }

    public String getThirdChecker() {
        return thirdChecker;
    }

    public void setThirdChecker(String thirdChecker) {
        this.thirdChecker = thirdChecker;
    }

    public Integer getThirdCheckStatus() {
        return thirdCheckStatus;
    }

    public void setThirdCheckStatus(Integer thirdCheckStatus) {
        this.thirdCheckStatus = thirdCheckStatus;
    }

    public Date getThirdCheckTime() {
        return thirdCheckTime;
    }

    public void setThirdCheckTime(Date thirdCheckTime) {
        this.thirdCheckTime = thirdCheckTime;
    }
}
