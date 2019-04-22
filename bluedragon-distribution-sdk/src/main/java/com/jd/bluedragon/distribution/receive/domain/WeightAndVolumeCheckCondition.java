package com.jd.bluedragon.distribution.receive.domain;

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
