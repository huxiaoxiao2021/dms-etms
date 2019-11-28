package com.jd.bluedragon.distribution.weightAndVolumeCheck;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName: WeightAndVolumeCheckCondition.java
 * @Description: 揽收重量校验统计-查询条件
 * @author: hujiping
 * @date: 2019/2/27 11:21
 */
public class WeightAndVolumeCheckCondition extends BasePagerCondition {

    private static final long serialVersionUID = 1L;

    /** 所属区域 */
    private Long reviewOrgCode;

    /** 所属分拣中心 */
    private Long createSiteCode;

    /** 是否超标 */
    private Integer isExcess;

    /** 复核日期 */
    private String startTime;

    /** 复核日期 */
    private String endTime;

    private Date reviewStartTime;
    private Date reviewEndTime;

    /** 运单号 */
    private String waybillCode;
    /** 扫描条码 */
    private String waybillOrPackCode;
    /** 商家名称 */
    private String busiName;
    /** 复核人ERP */
    private String reviewErp;
    /** 计费人ERP */
    private String billingErp;

    /** 抽检类型 */
    private Integer spotCheckType;

    public Integer getSpotCheckType() {
        return spotCheckType;
    }

    public void setSpotCheckType(Integer spotCheckType) {
        this.spotCheckType = spotCheckType;
    }

    public String getWaybillOrPackCode() {
        return waybillOrPackCode;
    }

    public void setWaybillOrPackCode(String waybillOrPackCode) {
        this.waybillOrPackCode = waybillOrPackCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getBusiName() {
        return busiName;
    }

    public void setBusiName(String busiName) {
        this.busiName = busiName;
    }

    public String getReviewErp() {
        return reviewErp;
    }

    public void setReviewErp(String reviewErp) {
        this.reviewErp = reviewErp;
    }

    public String getBillingErp() {
        return billingErp;
    }

    public void setBillingErp(String billingErp) {
        this.billingErp = billingErp;
    }

    public Long getReviewOrgCode() {
        return reviewOrgCode;
    }

    public void setReviewOrgCode(Long reviewOrgCode) {
        this.reviewOrgCode = reviewOrgCode;
    }

    public Long getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Long createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public Integer getIsExcess() {
        return isExcess;
    }

    public void setIsExcess(Integer isExcess) {
        this.isExcess = isExcess;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        try {
            setReviewStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime));
        } catch (ParseException e) {
        }
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        try {
            setReviewEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime));
        } catch (ParseException e) {
        }
        this.endTime = endTime;
    }

    public Date getReviewStartTime() {
        return reviewStartTime;
    }

    public void setReviewStartTime(Date reviewStartTime) {
        this.reviewStartTime = reviewStartTime;
    }

    public Date getReviewEndTime() {
        return reviewEndTime;
    }

    public void setReviewEndTime(Date reviewEndTime) {
        this.reviewEndTime = reviewEndTime;
    }
}
